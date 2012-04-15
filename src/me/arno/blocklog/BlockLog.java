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
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import me.arno.blocklog.commands.*;
import me.arno.blocklog.database.*;
import me.arno.blocklog.listeners.*;
import me.arno.blocklog.logs.LoggedBlock;
import me.arno.blocklog.logs.LoggedInteraction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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
	
	public HashMap<String, Plugin> softDepends = new HashMap<String, Plugin>();
	
	public String newVersion;
	public String currentVersion;
	public double doubleNewVersion;
	public double doubleCurrentVersion;
	
	public int autoSave = 0;
	public boolean saving = false;
	
	public Config cfg;
	
	@Override
	public FileConfiguration getConfig() {
		return cfg.getConfig();
	}
	
	@Override
	public void saveConfig() {
		cfg.saveConfig();
	}
	
	@Override
	public void reloadConfig() {
		cfg.reloadConfig();
	}
	
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
	
	private void loadConfiguration() {
		cfg = new Config();
		cfg.createDefaults();
		cfg.saveConfig();
		
		if(cfg.getConfig().getBoolean("blocklog.autosave.enabled")) {
			autoSave = cfg.getConfig().getInt("blocklog.autosave.blocks");
		}
	}
	
	private void loadDatabase() {
		String DBType = cfg.getConfig().getString("database.type");
		Statement stmt;
		try {
			if(DBType.equalsIgnoreCase("mysql")) {
		    	conn = DatabaseSettings.getConnection();
		    	stmt = conn.createStatement();
				
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_blocks.sql"));
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_rollbacks.sql"));
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_interactions.sql"));
				stmt.executeUpdate(getResourceContent("MySQL/blocklog_reports.sql"));
			} else if(DBType.equalsIgnoreCase("sqlite")) {
			    conn = DatabaseSettings.getConnection();
			    stmt = conn.createStatement();
				
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_blocks.sql"));
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_rollbacks.sql"));
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_interactions.sql"));
				stmt.executeUpdate(getResourceContent("SQLite/blocklog_reports.sql"));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateDatabase() {
		try {
			Statement stmt = conn.createStatement();
			
			Config versions = new Config("VERSIONS");
			versions.getConfig().addDefault("database", 2);
			versions.getConfig().options().copyDefaults(true);
			if(versions.getConfig().getInt("database") > 2) {
				log.info("Updating the database to version 2");
				if(DatabaseSettings.DBType().equalsIgnoreCase("mysql")) 
					stmt.executeUpdate("ALTER TABLE `blocklog_blocks` CHANGE `rollback_id` `rollback_id` INT(11) NOT NULL DEFAULT '0'");
				
				versions.getConfig().set("database", 2);
			}
			versions.saveConfig();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private String loadLatestVersion() {
        String pluginUrlString = "http://dev.bukkit.org/server-mods/block-log/files.rss";
        try {
            URL url = new URL(pluginUrlString);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element) firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return firstNodes.item(0).getNodeValue().replace("BlockLog", "").trim();
            }
        } catch (Exception e) {
        	// Nothing
        }
        return currentVersion;
    }
	
	private void loadDependencies() {
		ArrayList<String> plugins = new ArrayList<String>();
    	plugins.add("GriefPrevention");
    	
    	for(String plugin : plugins) {
    		if(getServer().getPluginManager().isPluginEnabled(plugin)) {
    			softDepends.put("GriefPrevention", getServer().getPluginManager().getPlugin("GriefPrevention"));
    		}
    	}
	}
	
	public boolean reloadPlugin() {
		if(saving)
			return false;
		try {
			getServer().getScheduler().cancelTasks(this);
			conn.close();
			softDepends.clear();
			
			log.info("Reloading the configurations");
		    loadConfiguration();
		    
			log.info("Reloading the database");
		    loadDatabase();
		    
		    log.info("Reloading the dependencies");
		    loadDependencies();
		    
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void loadPlugin() {
		currentVersion = getDescription().getVersion();
		log = getLogger();
		
		log.info("Loading the configurations");
	    loadConfiguration();
	    
		log.info("Loading the database");
	    loadDatabase();
	    updateDatabase();
	    
	    log.info("Loading the dependencies");
	    loadDependencies();
	    
	    if(getConfig().getBoolean("blocklog.updates")) {
		    try {
			    log.info("Checking for updates");
				newVersion = loadLatestVersion();
				
				doubleCurrentVersion = Double.valueOf(currentVersion.replaceFirst("\\.", ""));
				doubleNewVersion = Double.valueOf(newVersion.replaceFirst("\\.", ""));
				
				if(doubleNewVersion > doubleCurrentVersion) {
					log.warning("BlockLog v" + newVersion + " is released! You're using BlockLog v" + currentVersion);
					log.warning("Update BlockLog at http://dev.bukkit.org/server-mods/block-log/");
				}
		    } catch(Exception e) {
				// Nothing
			}
	    }
	    
		log.info("Starting BlockLog");
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
    	getCommand("blconvert").setExecutor(new CommandConvert(this));
    	getCommand("blreport").setExecutor(new CommandReport(this));
    	getCommand("blread").setExecutor(new CommandRead(this));
    	getCommand("blsearch").setExecutor(new CommandSearch(this));
    	
    	getServer().getPluginManager().registerEvents(new LogListener(this), this);
    	getServer().getPluginManager().registerEvents(new WandListener(this), this);
    	
    	if(getConfig().getBoolean("blocklog.updates"))
    		getServer().getPluginManager().registerEvents(new LoginListener(this), this);
    }
	
	public void saveLogs(final int count) {
		saveLogs(count, null, false);
	}
	
	public void saveLogs(final int count, final Player player) {
		saveLogs(count, player, false);
	}
	
	public void saveLogs(final int count, final boolean force) {
		saveLogs(count, null, force);
	}
	
	public void saveLogs(final int count, final Player player, final boolean force) {
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			public void run() {
				if((force == false && saving == true) && player != null) {
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "We're already saving some of the blocks.");
				} else if(force == false && saving == true && player == null) {
					log.info("We're already saving some of the blocks.");
				} else if(force == true || saving == false) {
					saving = true;
					if(player == null)
						log.info("Saving " + ((count == 0) ? "all the" : count) + " block edits!");
					else
						player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving " + ((count == 0) ? "all the" : count) + " block edits!");
					
					if(count == 0) {
			    		while(interactions.size() > 0) {
			    			LoggedInteraction interaction = interactions.get(0);
					    	interaction.save();
					    	interactions.remove(0);
			    		}
						while(blocks.size() > 0) {
				    		LoggedBlock block = blocks.get(0);
						    block.save();
						    blocks.remove(0);
			    		}
			    	} else {
			    		if(interactions.size() > 0) {
			    			for(int i=count; i!=0; i--) {
			    				LoggedInteraction interaction = interactions.get(0);
					    		interaction.save();
						    	interactions.remove(0);
			    			}
			    		}
			    		if(blocks.size() > 0) {
			    			for(int i=count; i!=0; i--) {
				    			LoggedBlock block = blocks.get(0);
						    	block.save();
						    	blocks.remove(0);
			    			}
			    		}
			    	}
					
					if(player == null)
						log.info("Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
					else
						player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
					
					saving = false;
				}
		    }
		});
	}
	
	@Override
	public void onEnable() {
		loadPlugin();
		PluginDescriptionFile PluginDesc = getDescription();
		log.info("v" + PluginDesc.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable() {
		saveLogs(0, true);
		PluginDescriptionFile PluginDesc = getDescription();
		log.info("v" + PluginDesc.getVersion() + " is disabled!");
	}
	
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
