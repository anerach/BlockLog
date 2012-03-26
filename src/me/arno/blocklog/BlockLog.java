package me.arno.blocklog;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import me.arno.blocklog.commands.CommandAutoSave;
import me.arno.blocklog.commands.CommandClear;
import me.arno.blocklog.commands.CommandConfig;
import me.arno.blocklog.commands.CommandHelp;
import me.arno.blocklog.commands.CommandRadiusRollback;
import me.arno.blocklog.commands.CommandReload;
import me.arno.blocklog.commands.CommandRollback;
import me.arno.blocklog.commands.CommandSave;
import me.arno.blocklog.commands.CommandStorage;
import me.arno.blocklog.commands.CommandUndo;
import me.arno.blocklog.commands.CommandWand;
import me.arno.blocklog.database.DatabaseSettings;
import me.arno.blocklog.database.PushBlocks;
import me.arno.blocklog.listeners.LogListener;
import me.arno.blocklog.listeners.LoginListener;
import me.arno.blocklog.listeners.WandListener;
import me.arno.blocklog.log.LoggedBlock;
import me.arno.blocklog.log.LoggedInteraction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BlockLog extends JavaPlugin {
	public Logger log;
	public DatabaseSettings dbSettings;
	public Connection conn;
	
	public ArrayList<String> users = new ArrayList<String>();
	public ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	public ArrayList<LoggedInteraction> interactions = new ArrayList<LoggedInteraction>();
	
	public String newVersion;
	public String currentVersion;
	
	public int autoSave = 0;
	
	public String getResourceContent(String file) {
		try {
			InputStream ResourceFile = getResource("resources/" + file);
			 
			final char[] buffer = new char[0x10000];
			StringBuilder StrBuilder = new StringBuilder();
			Reader InputReader = new InputStreamReader(ResourceFile, "UTF-8");
			int read;
			do {
				read = InputReader.read(buffer, 0, buffer.length);
				if (read > 0)
					StrBuilder.append(buffer, 0, read);
				
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
	    getConfig().addDefault("blocklog.leaves", false);
	    getConfig().addDefault("blocklog.warning.blocks", 500);
	    getConfig().addDefault("blocklog.warning.repeat", 100);
	    getConfig().addDefault("blocklog.warning.delay", 30);
	    getConfig().addDefault("blocklog.autosave.enabled", true);
	    getConfig().addDefault("blocklog.autosave.blocks", 1000);
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(getConfig().getBoolean("blocklog.autosave.enabled")) {
			autoSave = getConfig().getInt("blocklog.autosave.blocks");
		}
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
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_interactions.sql"));
			} else if(DBType.equalsIgnoreCase("sqlite")) {
			    conn = DatabaseSettings.getConnection(this);
			    stmt = conn.createStatement();
				
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_blocks.sql"));
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_rollbacks.sql"));
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_interactions.sql"));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try { // Update database
			if(DBType.equalsIgnoreCase("mysql")) {
		    	conn = DatabaseSettings.getConnection(this);
		    	stmt = conn.createStatement();
		    	stmt.executeUpdate("ALTER TABLE `blocklog_blocks` ADD `datavalue` INT(11) NOT NULL AFTER `block_id`");
			} else if(DBType.equalsIgnoreCase("sqlite")) {
			    conn = DatabaseSettings.getConnection(this);
			    stmt = conn.createStatement();
			    stmt.executeUpdate("ALTER TABLE 'blocklog_blocks' ADD COLUMN 'datavalue' INTEGER NOT NULL DEFAULT '0'");
		    }
			
		} catch (SQLException e) {
			//Prints error if table already exists
		}
	}
	
	public String loadLatestVersion(String currentVersion) {
        String pluginUrlString = "http://dev.bukkit.org/server-mods/block-log/files.rss";
        try {
            URL url = new URL(pluginUrlString);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element)firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return firstNodes.item(0).getNodeValue().replace("BlockLog", "").trim();
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return currentVersion;
    }
	
	public void loadPlugin() {
		double tmpCurrentVersion;
		double tmpNewVersion;
		currentVersion = getDescription().getVersion();
		log = getLogger();
		
		log.info("Loading the configurations");
	    loadConfiguration();
	    
		log.info("Loading the database");
	    loadDatabase();
	    
		log.info("Checking for updates");
		newVersion = loadLatestVersion(currentVersion);
		
		tmpCurrentVersion = Double.valueOf(currentVersion.replaceFirst("\\.", ""));
		tmpNewVersion = Double.valueOf(newVersion.replaceFirst("\\.", ""));
		
		if(tmpNewVersion > tmpCurrentVersion) {
			log.warning("BlockLog v" + newVersion + " is released! You're using BlockLog v" + currentVersion);
			log.warning("Update BlockLog at http://dev.bukkit.org/server-mods/block-log/");
		}
    	
    	new PushBlocks(this);
    	
    	getCommand("blhelp").setExecutor(new CommandHelp(this));
    	getCommand("blrollback").setExecutor(new CommandRollback(this));
    	getCommand("blrollbackradius").setExecutor(new CommandRadiusRollback(this));
    	getCommand("blrb").setExecutor(new CommandRollback(this));
    	getCommand("blconfig").setExecutor(new CommandConfig(this));
    	getCommand("blcfg").setExecutor(new CommandConfig(this));
    	getCommand("blwand").setExecutor(new CommandWand(this));
    	getCommand("blstorage").setExecutor(new CommandStorage(this));
    	getCommand("blsave").setExecutor(new CommandSave(this));
    	getCommand("blfullsave").setExecutor(new CommandSave(this));
    	getCommand("blreload").setExecutor(new CommandReload(this));
    	getCommand("blclear").setExecutor(new CommandClear(this));
    	getCommand("blundo").setExecutor(new CommandUndo(this));
    	getCommand("blautosave").setExecutor(new CommandAutoSave(this));
    	
    	getServer().getPluginManager().registerEvents(new LogListener(this), this);
    	getServer().getPluginManager().registerEvents(new WandListener(this), this);
    	getServer().getPluginManager().registerEvents(new LoginListener(this), this);
    }
	
	public void saveLogs(final int count) {
		saveLogs(count, null);
	}
	
	public void saveLogs(final int count, final Player player) {
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			public void run() {
				if(player == null)
					log.info("Saving all the blocks");
				else
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving " + ((count == 0) ? "all the" : count) + " block edits!");
				
				if(count == 0) {
					if(blocks.size() > 0) {
		    			while(blocks.size() > 0) {
			    			LoggedBlock block = blocks.get(0);
					    	block.save();
					    	blocks.remove(0);
		    			}
					}
		    		if(interactions.size() > 0) {
		    			while(interactions.size() > 0) {
		    				LoggedInteraction interaction = interactions.get(0);
				    		interaction.save();
				    		interactions.remove(0);
		    			}
		    		}
		    	} else {
		    		if(blocks.size() > 0) {
		    			for(int i=count; i!=0; i--) {
			    			LoggedBlock block = blocks.get(0);
					    	block.save();
					    	blocks.remove(0);
		    			}
		    		}
		    		if(interactions.size() > 0) {
		    			for(int i=count; i!=0; i--) {
		    				LoggedInteraction interaction = interactions.get(0);
				    		interaction.save();
					    	interactions.remove(0);
		    			}
		    		}
		    	}
				
				if(player == null)
					log.info("Successfully saved all the blocks");
				else
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
				
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
		saveLogs(0);
		PluginDescriptionFile PluginDesc = this.getDescription();
		log.info("v" + PluginDesc.getVersion() + " is disabled!");
	}
	
	
	/* Blocklog command */
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
		
		player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "This server is using BlockLog v" + getDescription().getVersion() + " by Anerach");
		return true;
	}
}
