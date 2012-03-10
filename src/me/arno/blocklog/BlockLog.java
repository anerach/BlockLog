package me.arno.blocklog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.arno.blocklog.commands.CommandConfig;
import me.arno.blocklog.commands.CommandRollback;
import me.arno.blocklog.commands.CommandSave;
import me.arno.blocklog.commands.CommandWand;
import me.arno.blocklog.database.PushBlocks;
import me.arno.blocklog.listeners.LogListener;
import me.arno.blocklog.listeners.WandListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	public ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	
	public void loadConfiguration() {
		try {
			getConfig().addDefault("mysql.enabled", false);
			getConfig().addDefault("mysql.host", "localhost");
	    	getConfig().addDefault("mysql.username", "root");
	    	getConfig().addDefault("mysql.password", null);
	    	getConfig().addDefault("mysql.database", null);
	    	getConfig().addDefault("mysql.port", 3306);
	    	getConfig().addDefault("blocklog.wand", 369);
	    	getConfig().addDefault("blocklog.results", 5);
	    	getConfig().addDefault("database.delay", 1);
	    	getConfig().addDefault("database.warning", 300);
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
						"`world` varchar(75) NOT NULL," +
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
						"'world' VARCHAR(75) NOT NULL," +
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
    	
    	new PushBlocks(this);
    	
    	getCommand("blrollback").setExecutor(new CommandRollback(this));
    	getCommand("blrb").setExecutor(new CommandRollback(this));
    	getCommand("blconfig").setExecutor(new CommandConfig(this));
    	getCommand("blcfg").setExecutor(new CommandConfig(this));
    	getCommand("blwand").setExecutor(new CommandWand(this));
    	getCommand("blsave").setExecutor(new CommandSave(this));
    	getCommand("blfullsave").setExecutor(new CommandSave(this));
    	
    	getServer().getPluginManager().registerEvents(new LogListener(this), this);
    	getServer().getPluginManager().registerEvents(new WandListener(this), this);
    }
	
	@Override
	public void onEnable() {
		loadPlugin();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[BlockLog] v" + pdfFile.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable() {
		// Pushing all the blocks
		while(blocks.size() > 0) {
			LoggedBlock block = blocks.get(0);
	    	try {
		    	Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				
		    	if(getConfig().getBoolean("mysql.enabled"))
		    		stmt.executeUpdate("INSERT INTO blocklog (player, block_id, world, date, x, y, z, type) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', UNIX_TIMESTAMP(), " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ")");
				else
					stmt.executeUpdate("INSERT INTO blocklog (player, block_id, world, date, x, y, z, type) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', strftime('%s', 'now'), " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ")");
		    } catch (SQLException e) {
	    		log.info("[BlockLog][BlockToDatabase][SQL] Exception!");
				log.info("[BlockLog][BlockToDatabase][SQL] " + e.getMessage());
	    	}
	    	blocks.remove(0);
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[BlockLog] v" + pdfFile.getVersion() + " is disabled!");
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
	}
	
	public Connection getConnection() throws SQLException
	{
		if(getConfig().getBoolean("mysql.enabled"))
			return DriverManager.getConnection(url, user, pass);
		else
			return DriverManager.getConnection(url);
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
			} else if(args[0].equalsIgnoreCase("help") && (player.isOp() || player.hasPermission("blocklog.help"))) {
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Commands!");
				player.sendMessage(ChatColor.DARK_RED +"/blocklog help" + ChatColor.GOLD + " - Shows this message!");
				if(player.isOp() || player.hasPermission("blocklog.reload"))
					player.sendMessage(ChatColor.DARK_RED +"/blocklog reload" + ChatColor.GOLD + " - Reloads blocklog config file.");
				if(player.isOp() || player.hasPermission("blocklog.wand"))
					player.sendMessage(ChatColor.DARK_RED +"/blwand" + ChatColor.GOLD + " - Enables blocklog's wand.");
				if(player.isOp() || player.hasPermission("blocklog.save"))
					player.sendMessage(ChatColor.DARK_RED +"/blsave [amount]" + ChatColor.GOLD + " - Saves 25 or the specified amount of blocks.");
				if(player.isOp() || player.hasPermission("blocklog.fullsave"))
					player.sendMessage(ChatColor.DARK_RED +"/blfullsave" + ChatColor.GOLD + " - Saves all the blocks.");
				if(player.isOp() || player.hasPermission("blocklog.rollback"))
					player.sendMessage(ChatColor.DARK_RED +"/blrollback [player] <time> <sec/min/hour/day/week>" + ChatColor.GOLD + " - Restortes the whole server or edits by one player to a specified point.");
			}
		}
		return true;
	}
}
