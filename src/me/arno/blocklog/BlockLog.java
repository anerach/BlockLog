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

import me.arno.blocklog.commands.CommandAutoSave;
import me.arno.blocklog.commands.CommandClear;
import me.arno.blocklog.commands.CommandConfig;
import me.arno.blocklog.commands.CommandHelp;
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
	public Logger log;
	
	public ArrayList<String> users = new ArrayList<String>();
	public ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	
	public String NewVersion = null;
	public int autoSave = 0;
	public boolean autoSaveMsg = false;
	public Connection conn;
	
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
	    getConfig().addDefault("database.type", "SQLite");
	    getConfig().addDefault("database.delay", 1);
		getConfig().addDefault("mysql.host", "localhost");
	    getConfig().addDefault("mysql.username", "root");
	    getConfig().addDefault("mysql.password", "");
	    getConfig().addDefault("mysql.database", "");
	    getConfig().addDefault("mysql.port", 3306);
	   	getConfig().addDefault("blocklog.wand", 369);
	    getConfig().addDefault("blocklog.results", 5);
	    getConfig().addDefault("blocklog.warning", 500);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void loadDatabase() {
		String DBType = getConfig().getString("database.type");
		
		Statement stmt;
		try {
			if(DBType.equalsIgnoreCase("mysql")) {
		    	conn = DatabaseSettings.getConnection(this);
		    	stmt = conn.createStatement();
				
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_blocks.sql"));
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_rollbacks.sql"));
			} else if(DBType.equalsIgnoreCase("sqlite")) {
			    conn = DatabaseSettings.getConnection(this);
			    stmt = conn.createStatement();
				
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
    	loadDatabase();
    	
    	new PushBlocks(this);
    	
    	getCommand("blhelp").setExecutor(new CommandHelp(this));
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
    	getCommand("blautosave").setExecutor(new CommandAutoSave(this));
    	
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
	
	public void saveBlocks(final int blockCount) {
		final Connection conn = this.conn;
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
		    public void run() {
		    	while(blocks.size() > 0) {
					LoggedBlock block = blocks.get(0);
					try {
						Statement stmt = conn.createStatement();
		
						stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type, rollback_id) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', , " + block.getDate() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ", " + block.getRollback() + ")");
					} catch (SQLException e) {
						e.printStackTrace();
					}
					blocks.remove(0);
				}
		    }
		});
	}
	
	@Override
	public void onEnable() {
		loadPlugin();
		PluginDescriptionFile PluginDesc = this.getDescription();
		log.info("v" + PluginDesc.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable() {
		saveBlocks(0);
		PluginDescriptionFile PluginDesc = this.getDescription();
		log.info("v" + PluginDesc.getVersion() + " is disabled!");
	}
	
	
	/* Help command */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blocklog"))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "This server is using BlockLog v" + getDescription().getVersion() + " by Anerach");
		return true;
	}
}
