package me.arno.blocklog;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import me.arno.blocklog.Metrics.Graph;
import me.arno.blocklog.commands.*;
import me.arno.blocklog.listeners.*;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.managers.*;
import me.arno.blocklog.schedules.AliveSchedule;
import me.arno.blocklog.schedules.SaveSchedule;
import me.arno.blocklog.schedules.UpdatesSchedule;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockLog extends JavaPlugin {
	private static BlockLog plugin;
	public Logger log;
	private Connection conn;
	
	private SettingsManager settingsManager;
	private DatabaseManager databaseManager;
	private QueueManager queueManager;
	private DependencyManager dependencyManager;
	
	public HashMap<String, WandSettings> wandSettings = new HashMap<String, WandSettings>();
	
	private HashMap<Integer, Integer> schedules = new HashMap<Integer, Integer>();
	
	public int autoSave = 0;
	public boolean saving = false;
	
	public static BlockLog getInstance() {
		return plugin;
	}
	
	public Connection getConnection() {
		try {
			if(conn == null)
				conn = databaseManager.getConnection();
			else if(conn.isClosed())
				conn = databaseManager.getConnection();
			else if(!conn.isValid(1))
				conn = databaseManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public HashMap<Integer, Integer> getSchedules() {
		return schedules;
	}
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public QueueManager getQueueManager() {
		return queueManager;
	}
	
	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}
	
	private void loadConfiguration() {
		Config worldConfig;
		for(World world : getServer().getWorlds()) {
			worldConfig = new Config("worlds" + File.separator + world.getName() + ".yml");
			
			for(LogType type : LogType.values()) {
				if(type != LogType.EXPLOSION_CREEPER && type != LogType.EXPLOSION_FIREBALL && type != LogType.EXPLOSION_TNT) {
					worldConfig.getConfig().addDefault(type.name(), true);
				}
			}
			worldConfig.getConfig().options().copyDefaults(true);
			worldConfig.saveConfig();
		}
		
		getConfig().addDefault("database.host", "localhost");
	    getConfig().addDefault("database.username", "root");
	    getConfig().addDefault("database.password", "");
	    getConfig().addDefault("database.database", "bukkit");
	    getConfig().addDefault("database.prefix", "blocklog_");
	    getConfig().addDefault("database.port", 3306);
	    getConfig().addDefault("database.alive-check", "2h");
	    getConfig().addDefault("blocklog.wand", 19);
	   	getConfig().addDefault("blocklog.results", 5);
	   	getConfig().addDefault("blocklog.save-delay", "10s");
	   	getConfig().addDefault("blocklog.save-amount", 10);
	    getConfig().addDefault("blocklog.updates", true);
	    getConfig().addDefault("blocklog.metrics", true);
	    getConfig().addDefault("blocklog.dateformat", "%d-%m %H:%i");
	    getConfig().addDefault("blocklog.debug", false);
	    getConfig().addDefault("warning.blocks", 500);
	    getConfig().addDefault("warning.repeat", 100);
	    getConfig().addDefault("warning.delay", "30s");
	    getConfig().addDefault("auto-save.enabled", true);
	    getConfig().addDefault("auto-save.blocks", 1000);
	    getConfig().addDefault("auto-save.world-save", false);
	    getConfig().addDefault("purge.log", true);
	    getConfig().addDefault("purge.blocks", "0s");
	    getConfig().addDefault("purge.interactions", "0s");
	    getConfig().addDefault("purge.chests", "0s");
	    getConfig().addDefault("purge.data", "0s");
	    getConfig().options().copyDefaults(true);
	    saveConfig();
		
		if(getConfig().getBoolean("auto-save.enabled")) {
			autoSave = getConfig().getInt("auto-save.blocks");
		}
	}
	
	private void purgeDatabase() {
		try {
			getDatabaseManager().purge(DatabaseManager.purgeableTables);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadDatabase() {
		try {
		    conn = databaseManager.getConnection();
	    	Statement stmt = conn.createStatement();
	    	
	    	for(String table : DatabaseManager.databaseTables) {
	    		stmt.executeUpdate(Util.getResourceContent("database/blocklog_" + table + ".sql").replace("{prefix}", DatabaseManager.databasePrefix));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateDatabase() {
		Config versions = new Config("VERSIONS");
		versions.getConfig().addDefault("database", 1);
		versions.getConfig().options().copyDefaults(true);
		
		// Functions here
		
		versions.saveConfig();
	}
	
	public void loadMetrics() {
		try {
		    Metrics metrics = new Metrics(this);
		    
		    // Storage Graph
		    Graph graph = metrics.createGraph("Storage");
		    graph.addPlotter(new Metrics.Plotter("Block Edits") {
		    	@Override
		        public int getValue() {
		        	try {
						return new Query(DatabaseManager.databasePrefix + "blocks").getRowCount();
					} catch (SQLException e) { e.printStackTrace(); }
		        	return 0;
		        }

		    });
		    graph.addPlotter(new Metrics.Plotter("Block Interactions") {
			    @Override
			    public int getValue() {
			    	try {
						return new Query(DatabaseManager.databasePrefix + "interactions").getRowCount();
			    	} catch (SQLException e) { e.printStackTrace(); }
			    	return 0;
			    }
		    });
		    graph.addPlotter(new Metrics.Plotter("Chest Items") {
			    @Override
			    public int getValue() {
			    	try {
						return new Query(DatabaseManager.databasePrefix + "chests").getRowCount();
			    	} catch (SQLException e) { e.printStackTrace(); }
			    	return 0;
			    }
		    });
		    graph.addPlotter(new Metrics.Plotter("Other Data") {
			    @Override
			    public int getValue() {
			    	try {
						return new Query(DatabaseManager.databasePrefix + "data").getRowCount();
			    	} catch (SQLException e) { e.printStackTrace(); }
			    	return 0;
			    }
		    });
		    metrics.start();
		} catch (IOException e) {
			log.warning("Unable to submit the statistics");
		}
	}
	
	public boolean reloadPlugin() {
		if(saving)
			return false;
		
		getServer().getScheduler().cancelTasks(this);
		
		log.info("Reloading the configurations");
		loadConfiguration();
		    
		log.info("Reloading the database");
		loadDatabase();
		return true;
	}
	
	private void loadPlugin() {
		BlockLog.plugin = this;
		log = getLogger();
		
		log.info("Loading the configurations");
	    loadConfiguration();
		
		log.info("Loading the managers");
		settingsManager = new SettingsManager();
		databaseManager = new DatabaseManager();
		queueManager = new QueueManager();
		dependencyManager = new DependencyManager();
	    
	    log.info("Loading the database");
	    loadDatabase();
	    updateDatabase();
	    
	    log.info("Purging the database");
	    purgeDatabase();
		
		if(getConfig().getBoolean("blocklog.metrics")) {
			log.info("Loading metrics");
			loadMetrics();
		}
	    
		log.info("Starting BlockLog");
		
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Thread(new SaveSchedule(getSettingsManager().getBlockSaveAmount())), 200L, getSettingsManager().getBlockSaveDelay() * 20L);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Thread(new AliveSchedule()), 100L, getSettingsManager().getDatabaseAliveCheckInterval() * 20L);

		getServer().getPluginManager().registerEvents(new BlockListener(), this);
    	getServer().getPluginManager().registerEvents(new ChestListener(), this);
    	getServer().getPluginManager().registerEvents(new EntityListener(), this);
    	getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    	getServer().getPluginManager().registerEvents(new WandListener(), this);
    	getServer().getPluginManager().registerEvents(new WorldListener(), this);
    	
    	if(getDependencyManager().isDependencyEnabled("mcMMO"))
    		getServer().getPluginManager().registerEvents(new McMMOListener(), this);
    	
    	if(getSettingsManager().isUpdatesEnabled()) {
	    	getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdatesSchedule(getDescription().getVersion()), 1L, 1L * 60L * 60L * 20L); // Check every hour for a new version
	    }
    }
	
	public void saveLogs(final int count) {
		saveLogs(count, getServer().getConsoleSender());
	}
	
	public void saveLogs(final int count, final CommandSender sender) {
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Thread(new SaveSchedule(count, sender)));
	}
	
	private void stopPlugin() {
		try {
			getServer().getScheduler().cancelTasks(this);
			
			log.info("Saving all the queued logs!");
			while(!getQueueManager().isQueueEmpty())
				getQueueManager().saveQueue();
			
			log.info("Successfully saved all the queued logs!");
			
			if(conn != null && !conn.isClosed())
				conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		BlockLog.plugin = null;
	}
	
	@Override
	public void onEnable() {
		loadPlugin();
		PluginDescriptionFile PluginDesc = getDescription();
		log.info("v" + PluginDesc.getVersion() + " is enabled!");
	}
	
	@Override
	public void onDisable() {
		stopPlugin();
		PluginDescriptionFile PluginDesc = getDescription();
		log.info("v" + PluginDesc.getVersion() + " is disabled!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("blocklog"))
			return false;
		
		if(args.length < 1) {
			sender.sendMessage(ChatColor.GOLD + "Say " + ChatColor.BLUE + "/bl help " + ChatColor.GOLD + "for a list of available commands");
			sender.sendMessage(ChatColor.GOLD + "This server is using BlockLog v" + getDescription().getVersion() + " by Anerach");
			return true;
		}
		
		commandLabel = args[0];
		
		ArrayList<String> argList = new ArrayList<String>(Arrays.asList(args));
		argList.remove(0);
		args = argList.toArray(new String[]{});
		
		BlockLogCommand command = new BlockLogCommand();
		
		if(commandLabel.equalsIgnoreCase("help") || commandLabel.equalsIgnoreCase("h"))
			command = new CommandHelp();
		else if(commandLabel.equalsIgnoreCase("autosave"))
			command = new CommandAutoSave();
		else if(commandLabel.equalsIgnoreCase("cancel"))
			command = new CommandCancel();
		else if(commandLabel.equalsIgnoreCase("config") || commandLabel.equalsIgnoreCase("cfg"))
			command = new CommandConfig();
		else if(commandLabel.equalsIgnoreCase("lookup"))
			command = new CommandLookup();
		else if(commandLabel.equalsIgnoreCase("purge"))
			command = new CommandPurge();
		else if(commandLabel.equalsIgnoreCase("queue"))
			command = new CommandQueue();
		else if(commandLabel.equalsIgnoreCase("reload"))
			command = new CommandReload();
		else if(commandLabel.equalsIgnoreCase("rollback") || commandLabel.equalsIgnoreCase("rb"))
			command = new CommandRollback();
		else if(commandLabel.equalsIgnoreCase("rollbacklist") || commandLabel.equalsIgnoreCase("rblist") || commandLabel.equalsIgnoreCase("rbl"))
			command = new CommandRollbackList();
		else if(commandLabel.equalsIgnoreCase("save"))
			command = new CommandSave();
		else if(commandLabel.equalsIgnoreCase("search"))
			command = new CommandSearch();
		else if(commandLabel.equalsIgnoreCase("simrollback") || commandLabel.equalsIgnoreCase("simrb"))
			command = new CommandSimulateRollback();
		else if(commandLabel.equalsIgnoreCase("simundo"))
			command = new CommandSimulateUndo();
		else if(commandLabel.equalsIgnoreCase("storage"))
			command = new CommandStorage();
		else if(commandLabel.equalsIgnoreCase("undo"))
			command = new CommandUndo();
		else if(commandLabel.equalsIgnoreCase("wand"))
			command = new CommandWand();
		
		cmd.setUsage(command.getCommandUsage());
		return command.execute(sender, cmd, args);
	}
}
