package me.arno.blocklog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.arno.blocklog.commands.CommandClear;
import me.arno.blocklog.commands.CommandConfig;
import me.arno.blocklog.commands.CommandRadiusRollback;
import me.arno.blocklog.commands.CommandReload;
import me.arno.blocklog.commands.CommandRollback;
import me.arno.blocklog.commands.CommandSave;
import me.arno.blocklog.commands.CommandUndo;
import me.arno.blocklog.commands.CommandWand;
import me.arno.blocklog.database.DatabaseSettings;
import me.arno.blocklog.database.PushBlocks;
import me.arno.blocklog.listeners.LogListener;
import me.arno.blocklog.listeners.LoginListener;
import me.arno.blocklog.listeners.WandListener;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockLog extends JavaPlugin {
	public Logger log; //Logger.getLogger("Minecraft");
	
	public DatabaseSettings dbSettings;
	
	public ArrayList<String> users = new ArrayList<String>();
	public ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	
	public String NewVersion = null;
	
	public String getResourceContent(String file) {
		try {
			InputStream ResourceFile = getResource("resources/" + file);
			 
			final char[] buffer = new char[0x10000];
			StringBuilder StrBuilder = new StringBuilder();
			Reader InputReader = new InputStreamReader(ResourceFile, "UTF-8");
			int read;
			do {
				read = InputReader.read(buffer, 0, buffer.length);
				if (read > 0) {
					StrBuilder.append(buffer, 0, read);
				}
			} while (read >= 0);
			InputReader.close();
			ResourceFile.close();
			return StrBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
		    
		    dbSettings = new DatabaseSettings(this);
		    
		    if(dbSettings.MySQLEnabled()) {
			    Connection conn = dbSettings.getConnection();
				Statement stmt = conn.createStatement();
				

				stmt.executeUpdate("RENAME TABLE blocklog TO blocklog_blocks");
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_blocks.sql"));
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_rollbacks.sql"));
		    } else {
		    	Connection conn = dbSettings.getConnection();
				Statement stmt = conn.createStatement();
				
				stmt.executeUpdate("RENAME TABLE blocklog TO blocklog_blocks");
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_blocks.sql"));
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_rollbacks.sql"));
		    }
		} catch (SQLException e) {
			log.info(e.getMessage()); 
		}
	}
	
	public void loadPlugin() {
		log = getLogger();
    	loadConfiguration();
    	
    	new PushBlocks(this);
    	
    	getCommand("blrollback").setExecutor(new CommandRollback(this));
    	getCommand("blrollbackradius").setExecutor(new CommandRadiusRollback(this));
    	getCommand("blrb").setExecutor(new CommandRollback(this));
    	getCommand("blconfig").setExecutor(new CommandConfig(this));
    	getCommand("blcfg").setExecutor(new CommandConfig(this));
    	getCommand("blwand").setExecutor(new CommandWand(this));
    	getCommand("blsave").setExecutor(new CommandSave(this));
    	getCommand("blfullsave").setExecutor(new CommandSave(this));
    	getCommand("blreload").setExecutor(new CommandReload(this));
    	getCommand("blclear").setExecutor(new CommandClear(this));
    	getCommand("blundo").setExecutor(new CommandUndo(this));
    	
    	getServer().getPluginManager().registerEvents(new LogListener(this), this);
    	getServer().getPluginManager().registerEvents(new WandListener(this), this);
    	getServer().getPluginManager().registerEvents(new LoginListener(this), this);
    	
    	try {
    		URL url;
		
			url = new URL("http://dl.dropbox.com/u/24494712/LogBlock/version.txt");
		
	        URLConnection urlConnection = url.openConnection();
	        urlConnection.setConnectTimeout(1000);
	        urlConnection.setReadTimeout(1000);
	        BufferedReader breader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	
	        StringBuilder stringBuilder = new StringBuilder();
	
	        String line;
	        while((line = breader.readLine()) != null) {
	                stringBuilder.append(line);
	        }
	
	        int LatestVersion = Integer.parseInt(stringBuilder.toString().replace(".", ""));
	        int ThisVersion = Integer.parseInt(getDescription().getVersion().replace(".", ""));
	        if(LatestVersion > ThisVersion) {
	        	log.info("There is a new version of BlockLog available (v" + stringBuilder.toString() + ")");
	        	NewVersion = stringBuilder.toString();
	        }
	        	
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
	
	@Override
	public void onEnable() {
		loadPlugin();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("v" + pdfFile.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable() {
		// Pushing all the blocks
		while(blocks.size() > 0) {
			LoggedBlock block = blocks.get(0);
			try {
		    	Connection conn = dbSettings.getConnection();
				Statement stmt = conn.createStatement();
				
				stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type, rollback_id) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', , " + block.getDate() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ", " + block.getRollback() + ")");
		    } catch (SQLException e) {
	    		log.info("[BlockToDatabase][SQL] Exception!");
				log.info("[BlockToDatabase][SQL] " + e.getMessage());
	    	}
	    	blocks.remove(0);
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[BlockLog] v" + pdfFile.getVersion() + " is disabled!");
	}
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if((!commandLabel.equalsIgnoreCase("blocklog")) && (!commandLabel.equalsIgnoreCase("bl")))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Commands");
		player.sendMessage(ChatColor.DARK_RED +"/blhelp" + ChatColor.GOLD + " - Shows this message");
		if(player.isOp() || player.hasPermission("blocklog.reload"))
			player.sendMessage(ChatColor.DARK_RED +"/blocklog reload" + ChatColor.GOLD + " - Reloads blocklog config file");
		if(player.isOp() || player.hasPermission("blocklog.wand"))
			player.sendMessage(ChatColor.DARK_RED +"/blwand" + ChatColor.GOLD + " - Enables blocklog's wand");
		if(player.isOp() || player.hasPermission("blocklog.save"))
			player.sendMessage(ChatColor.DARK_RED +"/blsave [amount]" + ChatColor.GOLD + " - Saves 25 or the specified amount of blocks");
		if(player.isOp() || player.hasPermission("blocklog.fullsave"))
			player.sendMessage(ChatColor.DARK_RED +"/blfullsave" + ChatColor.GOLD + " - Saves all the blocks");
		if(player.isOp() || player.hasPermission("blocklog.rollback")) {
			player.sendMessage(ChatColor.DARK_RED +"/blrollback [player] <time> <sec|min|hour|day|week>" + ChatColor.GOLD + " - Restortes the whole server or edits by one player to a specified point");
			player.sendMessage(ChatColor.DARK_RED +"/blrollbackradius [player] <time> <sec|min|hour|day|week>" + ChatColor.GOLD + " - Restortes the whole server or edits by one player to a specified point");
		}
		if(player.isOp() || player.hasPermission("blocklog.clear"))
			player.sendMessage(ChatColor.DARK_RED +"/blclear <amount> <day|week>" + ChatColor.GOLD + " - Clears blocklog's history");
		if(player.isOp() || player.hasPermission("blocklog.undo"))
			player.sendMessage(ChatColor.DARK_RED +"/blundo [rollback]" + ChatColor.GOLD + " - Undo's the latest or specified rollback");
		if(player.isOp() || player.hasPermission("blocklog.config"))
			player.sendMessage(ChatColor.DARK_RED +"/blundo <get/set> <key> [value]" + ChatColor.GOLD + " - Change blocklog's command values ingame");
		return true;
	}
}
