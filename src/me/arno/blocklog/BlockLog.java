package me.arno.blocklog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockLog extends JavaPlugin {
	public final Logger log = Logger.getLogger("Minecraft");
	
	public String user;
	public String pass;
	public String url;
	
	public ArrayList<String> users = new ArrayList<String>();
	
	public void loadConfiguration() {
		try {
			getConfig().addDefault("mysql.enabled", false);
			getConfig().addDefault("mysql.host", "localhost");
	    	getConfig().addDefault("mysql.username", "root");
	    	getConfig().addDefault("mysql.password", null);
	    	getConfig().addDefault("mysql.database", null);
	    	getConfig().addDefault("mysql.port", 3306);
	    	//getConfig().addDefault("blocklog.wand", 369);
	    	getConfig().addDefault("blocklog.results", 5);
		    getConfig().options().copyDefaults(true);
		    saveConfig();
		    
		    if(getConfig().getBoolean("mysql.enabled")) {
		    	user = getConfig().getString("mysql.username");
			    pass = getConfig().getString("mysql.password");
			    url = "jdbc:mysql://" + getConfig().getString("mysql.host") + ":" + getConfig().getString("mysql.port") + "/" + getConfig().getString("mysql.database");
			    Connection conn = DriverManager.getConnection(url, user, pass);
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS `blocklog` (" +
						"`id` int(11) NOT NULL AUTO_INCREMENT," +
						"`player` varchar(75) NOT NULL," +
						"`block_id` int(11) NOT NULL," +
						"`type` tinyint(1) NOT NULL," +
						"`rollback_id` tinyint(1) NOT NULL DEFAULT '0'," +
						"`x` double NOT NULL," +
						"`y` double NOT NULL," +
						"`z` double NOT NULL," +
						"`date` int(11) NOT NULL," +
						"PRIMARY KEY (`id`)" +
						") ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
		    } else {
		    	Class.forName("org.sqlite.JDBC");
		    	
		    	url = "jdbc:sqlite:plugins/BlockLog/blocklog.db";
		    	
		    	Connection conn = DriverManager.getConnection(url);
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS 'blocklog' (" +
						"'id' INTEGER PRIMARY KEY NOT NULL," +
						"'player' VARCHAR(75) NOT NULL," +
						"'block_id' INTEGER NOT NULL," +
						"'type' INTEGER NOT NULL," +
						"'rollback_id' INTEGER NOT NULL DEFAULT '0'," +
						"'x' DOUBLE NOT NULL," +
						"'y' DOUBLE NOT NULL," +
						"'z' DOUBLE NOT NULL," +
						"'date' INTEGER NOT NULL" +
						");");
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlugin() {
    	loadConfiguration();
    	
		getServer().getPluginManager().registerEvents(new BlockLogListener(this), this);
    }
	
	@Override
	public void onEnable() {
		loadPlugin();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[BlockLog] v" + pdfFile.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[BlockLog] v" + pdfFile.getVersion() + " is disabled!");
	}
	
	public Connection getConnection() throws SQLException
	{
		if(getConfig().getBoolean("mysql.enabled"))
			return DriverManager.getConnection(url, user, pass);
		else
			return DriverManager.getConnection(url);
	}
	
	public String getQuery(String str) {
		if(getConfig().getBoolean("mysql.enabled")) {
			if(str.equalsIgnoreCase("PlayerRollback"))
				return "SELECT `block_id`,`type`,`x`,`y`,`z` FROM `blocklog` WHERE `date` > '%s' AND `rollback_id` = 0 AND `player` = '%s' ORDER BY `date` DESC";
			else if(str.equalsIgnoreCase("TotalRollback"))
				return "SELECT `block_id`,`type`,`x`,`y`,`z` FROM `blocklog` WHERE `date` > '%s' AND `rollback_id` = 0 ORDER BY `date` DESC";
		} else {
			if(str.equalsIgnoreCase("PlayerRollback"))
				return "SELECT block_id,type,x,y,z FROM blocklog WHERE date > '%s' AND rollback_id = 0 AND player = '%s' ORDER BY date DESC";
			else if(str.equalsIgnoreCase("TotalRollback"))
				return "SELECT block_id,type,x,y,z FROM blocklog WHERE date > '%s' AND rollback_id = 0 ORDER BY date DESC";
		}
		
		return "";
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if((!commandLabel.equalsIgnoreCase("blocklog")) && (!commandLabel.equalsIgnoreCase("bl")))
			return true;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("reload") && (player.isOp() || player.hasPermission("blocklog.reload"))) {
				log.info("[BlockLog] Reloading!");
				loadPlugin();
				log.info("[BlockLog] Reloaded Succesfully!");
			} else if (args[0].equalsIgnoreCase("wand") && (player.isOp() || player.hasPermission("blocklog.wand"))) {
				if(users.isEmpty()) {
					users.add(player.getName());
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
				} else if(users.contains(player.getName())) {
					users.remove(player.getName());
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand disabled!");
				} else {
					users.add(player.getName());
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
				}
			} else if (args[0].equalsIgnoreCase("rollback") && (player.isOp() || player.hasPermission("blocklog.rollback"))) {
				try {
					if(args.length > 2) {
						Connection conn = getConnection();
							
						Statement stmt = conn.createStatement();
						ResultSet rs;
						int time;
						
						Set<String> Second = new HashSet<String>(Arrays.asList("s", "sec","secs","second","seconds"));
						Set<String> Minute = new HashSet<String>(Arrays.asList("m", "min","mins","minute","minutes"));
						Set<String> Hour = new HashSet<String>(Arrays.asList("h", "hour","hours"));
						Set<String> Day = new HashSet<String>(Arrays.asList("d", "day","days"));
						Set<String> Week = new HashSet<String>(Arrays.asList("w", "week","weeks"));
						
						String timeVal = ((args.length > 3) ? args[3].toLowerCase() : args[2].toLowerCase());
						Integer timeInt = Integer.parseInt(((args.length > 3) ? args[2] : args[1].toLowerCase()));
						
						if(Second.contains(timeVal))
							time = (int) (System.currentTimeMillis()/1000 - timeInt);
						else if(Minute.contains(timeVal))
							time = (int) (System.currentTimeMillis()/1000 - timeInt * 60);
						else if(Hour.contains(timeVal))
							time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60);
						else if(Day.contains(timeVal))
							time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24);
						else if(Week.contains(timeVal))
							time = (int) (System.currentTimeMillis()/1000 - timeInt * 60 * 60 * 24 * 7);
						else {
							player.sendMessage(ChatColor.DARK_GREEN + "Invalid time");
							return true;
						}
						
						if(args.length > 3)
							rs = stmt.executeQuery(String.format(getQuery("PlayerRollback"), time, args[1]));
						else
							rs = stmt.executeQuery(String.format(getQuery("TotalRollback"), time));
						
						int i = 0;
						while(rs.next()) {
							Material m = Material.getMaterial(rs.getInt("block_id"));
							int type = rs.getInt("type");
							if(type == 0)
								player.getWorld().getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(m);
							else
								player.getWorld().getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(Material.getMaterial(0));
								
							i++;
						}
						player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GREEN + i + ChatColor.GOLD + " blocks changed!");
						//TODO Confirm/Cancel changes directly!
						return true;
					}
					player.sendMessage(ChatColor.GOLD + "Commands");
					player.sendMessage(ChatColor.DARK_GREEN + "/blocklog wand");
					player.sendMessage(ChatColor.DARK_GREEN + "/blocklog rollback [player] <amount> <seconds/minutes/hours/days>");
					return true;
				} catch(SQLException ex) {
					log.info("[BlockLog][Command][Rollback][SQL] Exception!");
					log.info("[BlockLog][Command][Rollback][SQL] " + ex.getMessage());
				}
			}
		}
		return true;
	}
}
