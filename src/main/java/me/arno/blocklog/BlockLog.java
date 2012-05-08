package me.arno.blocklog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.arno.blocklog.commands.*;
import me.arno.blocklog.database.DatabaseSettings;
import me.arno.blocklog.listeners.*;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.logs.LoggedBlock;
import me.arno.blocklog.logs.LoggedInteraction;
import me.arno.blocklog.pail.PailInterface;
import me.arno.blocklog.schedules.Save;
import me.arno.blocklog.schedules.Updates;
import me.escapeNT.pail.Pail;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockLog extends JavaPlugin {
	public static BlockLog plugin;
	public Logger log;
	public DatabaseSettings dbSettings;
	public Connection conn;
	
	private SettingsManager settingsManager;
	
	public final String[] SQLTables = {"blocks", "rollbacks", "undos", "interactions", "reports", "chat", "deaths", "kills", "commands"};
	
	public ArrayList<String> users = new ArrayList<String>();
	public HashMap<String, ItemStack> playerItemStack = new HashMap<String, ItemStack>();
	public HashMap<String, Integer> playerItemSlot = new HashMap<String, Integer>();
	
	private ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	private ArrayList<LoggedInteraction> interactions = new ArrayList<LoggedInteraction>();
	
	private HashMap<Integer, Integer> schedules = new HashMap<Integer, Integer>();
	
	public HashMap<String, Plugin> softDepends = new HashMap<String, Plugin>();
	
	public String newVersion;
	public String currentVersion;
	public double doubleNewVersion;
	public double doubleCurrentVersion;
	
	public int autoSave = 0;
	public boolean saving = false;
	
	public void addBlock(LoggedBlock block) {
		blocks.add(block);
	}
	
	public void addInteraction(LoggedInteraction interaction) {
		interactions.add(interaction);
	}
	
	public ArrayList<LoggedBlock> getBlocks() {
		return blocks;
	}
	
	public ArrayList<LoggedInteraction> getInteractions() {
		return interactions;
	}
	
	public HashMap<Integer, Integer> getSchedules() {
		return schedules;
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
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	private void loadConfiguration() {
		settingsManager = new SettingsManager();
		
		Config worldConfig;
		for(World world : getServer().getWorlds()) {
			worldConfig = new Config("worlds" + File.pathSeparator + world.getName() + ".yml");
			
			for(LogType type : LogType.values()) {
				if(type != LogType.EXPLOSION_CREEPER && type != LogType.EXPLOSION_GHAST && type != LogType.EXPLOSION_TNT) {
					worldConfig.getConfig().addDefault(type.name(), true);
				}
			}
		}
		
		getConfig().addDefault("mysql.host", "localhost");
	    getConfig().addDefault("mysql.username", "root");
	    getConfig().addDefault("mysql.password", "");
	    getConfig().addDefault("mysql.database", "");
	    getConfig().addDefault("mysql.port", 3306);
	   	getConfig().addDefault("blocklog.wand", 19);
	   	getConfig().addDefault("blocklog.results", 5);
	   	getConfig().addDefault("blocklog.delay", 1);
	    getConfig().addDefault("blocklog.warning.blocks", 500);
	    getConfig().addDefault("blocklog.warning.repeat", 100);
	    getConfig().addDefault("blocklog.warning.delay", 30);
	    getConfig().addDefault("blocklog.autosave.enabled", true);
	    getConfig().addDefault("blocklog.autosave.blocks", 1000);
	    getConfig().addDefault("blocklog.reports", true);
	    getConfig().addDefault("blocklog.updates", true);
	    getConfig().addDefault("blocklog.metrics", true);
	    getConfig().addDefault("blocklog.dateformat", "%d-%m %H:%i");
	    getConfig().addDefault("cleanup.log", true);
	    getConfig().addDefault("cleanup.blocks.enabled", false);
	    getConfig().addDefault("cleanup.blocks.days", 14);
	    getConfig().addDefault("cleanup.interactions.enabled", false);
	    getConfig().addDefault("cleanup.interactions.days", 14);
	    getConfig().addDefault("cleanup.chat.enabled", false);
	    getConfig().addDefault("cleanup.chat.days", 14);
	    getConfig().addDefault("cleanup.deaths.enabled", false);
	    getConfig().addDefault("cleanup.deaths.days", 14);
	    getConfig().addDefault("cleanup.kills.enabled", false);
	    getConfig().addDefault("cleanup.kills.days", 14);
	    getConfig().options().copyDefaults(true);
	    saveConfig();
		
		if(getConfig().getBoolean("blocklog.autosave.enabled")) {
			autoSave = getConfig().getInt("blocklog.autosave.blocks");
		}
	}
	
	private void CleanUpDatabase() {
		Long currentTime = System.currentTimeMillis()/1000;
		String[] tables = new String[] {"blocks", "interactions", "chat", "deaths", "kills", "commands"};
		
		try {
			FileWriter fstream = new FileWriter("BlockLog Database Cleanup.log");
			BufferedWriter out = new BufferedWriter(fstream);
			
			Statement stmt = conn.createStatement();
			Integer time;
			
			for(String table : tables) {
				if(getConfig().getBoolean("cleanup." + table + ".enabled")) {
					time = getConfig().getInt("cleanup." + table + ".days") * 60 * 60 * 24;
					
					ResultSet rs = stmt.executeQuery("SELECT COUNT(id) AS count FROM blocklog_" + table + " WHERE date < " + (currentTime - time));
					rs.next();
					if(rs.getString("count") != null) {
						out.write("[BlockLog] Deleted " + rs.getString("count") + " results from blocklog_" + table + System.getProperty("line.separator"));
						
						stmt.executeUpdate("DELETE FROM blocklog_" + table + " WHERE date < " + (currentTime - time));
					}
				}
			}
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadDatabase() {
		try {
	    	conn = DatabaseSettings.getConnection();
	    	Statement stmt = conn.createStatement();
	    	
	    	for(String table : SQLTables) {
	    		stmt.executeUpdate(getResourceContent("database/blocklog_" + table + ".sql"));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateDatabase() {
		Config versions = new Config("VERSIONS");
		versions.getConfig().addDefault("database", 10);
		if(versions.getConfig().getInt("database") < 2) {
			// Update code here
		}
		versions.getConfig().options().copyDefaults(true);
		versions.saveConfig();
	}
	
	private void loadDependencies() {
		ArrayList<String> plugins = new ArrayList<String>();
    	plugins.add("GriefPrevention");
    	plugins.add("WorldGuard");
    	plugins.add("mcMMO");
    	plugins.add("Pail");
    	
    	for(String plugin : plugins) {
    		if(getServer().getPluginManager().isPluginEnabled(plugin)) {
    			softDepends.put(plugin, getServer().getPluginManager().getPlugin(plugin));
    		}
    	}
	}
	
	public boolean reloadPlugin() {
		if(saving)
			return false;
		
		getServer().getScheduler().cancelTasks(this);
		softDepends.clear();
		
		log.info("Reloading the configurations");
		loadConfiguration();
		    
		log.info("Reloading the database");
		loadDatabase();
		   
		log.info("Reloading the dependencies");
		loadDependencies();
		return true;
	}
	
	public void loadMetrics() {
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
	}
	
	private void loadPlugin() {
		plugin = this;
		currentVersion = getDescription().getVersion();
		log = getLogger();
	    
	    log.info("Loading the dependencies");
	    loadDependencies();
	    
	    log.info("Loading the configurations");
	    loadConfiguration();
		
		if(getConfig().getBoolean("blocklog.metrics")) {
			log.info("Loading metrics");
			loadMetrics();
		}
	    
	    log.info("Loading the database");
	    loadDatabase();
	    updateDatabase();
	    
	    if(softDepends.containsKey("Pail")) {
	    	log.info("Hooking into pail");
	    	loadPailInterface();
	    }
	    
	    log.info("Cleaning up the database");
	    CleanUpDatabase();
	    
	    if(getConfig().getBoolean("blocklog.updates")) {
	    	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Updates(), 1L, 1 * 60 * 60 * 20L); // Check every hour for a new version
	    }
	    
		log.info("Starting BlockLog");
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Save(this, 1, null, false), 100L, getConfig().getInt("blocklog.delay") * 20L);
    	
    	getServer().getPluginManager().registerEvents(new WandListener(this), this);
    	getServer().getPluginManager().registerEvents(new BlockListener(this), this);
    	getServer().getPluginManager().registerEvents(new InteractionListener(this), this);
    	getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    	
    	if(getConfig().getBoolean("blocklog.updates"))
    		getServer().getPluginManager().registerEvents(new NoticeListener(this), this);
    	if(softDepends.containsKey("mcMMO"))
    		getServer().getPluginManager().registerEvents(new McMMOListener(this), this);
    }
	
	public void saveLogs(final int count) {
		saveLogs(count, null);
	}
	
	public void saveLogs(final int count, final Player player) {
		getServer().getScheduler().scheduleAsyncDelayedTask(this, new Save(this, count, player));
	}
	
	private void stopPlugin() {
		try {
			getServer().getScheduler().cancelTasks(this);
			
			log.info("Saving all the block edits!");
			while(!interactions.isEmpty()) {
	    		LoggedInteraction interaction = interactions.get(0);
			    interaction.save();
			    interactions.remove(0);
	    	}
			while(!blocks.isEmpty()) {
				LoggedBlock block = blocks.get(0);
				block.save();
			  	blocks.remove(0);
			}
			log.info("Successfully saved all the block edits!");
			
			conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blocklog"))
			return false;
		
		if(args.length < 1 && player != null) {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "This server is using BlockLog v" + getDescription().getVersion() + " by Anerach");
			return true;
		}
		
		ArrayList<String> argList = new ArrayList<String>();
		
		if(args.length > 1) {
			for(int i=1;i<args.length;i++) {
				argList.add(args[i]);
			}
		}
		
		String[] newArgs = argList.toArray(new String[]{});
		
		BlockLogCommand command;
		
		if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
			command = new CommandHelp();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("autosave")) {
			command = new CommandAutoSave();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("cancel")) {
			command = new CommandCancel();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("clear")) {
			command = new CommandClear();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg")) {
			command = new CommandConfig();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("lookup")) {
			command = new CommandLookup();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("read")) {
			command = new CommandRead();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("reload")) {
			command = new CommandReload();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("report")) {
			command = new CommandReport();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("rollback") || args[0].equalsIgnoreCase("rb")) {
			command = new CommandRollback();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("rollbacklist") || args[0].equalsIgnoreCase("rblist") || args[0].equalsIgnoreCase("rbl")) {
			command = new CommandRollbackList();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("save")) {
			command = new CommandSave();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("search")) {
			command = new CommandSearch();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("storage")) {
			command = new CommandStorage();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("undo")) {
			command = new CommandUndo();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("wand")) {
			command = new CommandWand();
			command.setCommandUsage(command.getCommandUsage());
			return command.execute(player, cmd, newArgs);
		}
		return true;
	}
	
	public void loadPailInterface() {
		Pail pail = (Pail) softDepends.get("Pail");
		pail.loadInterfaceComponent("BlockLog", new PailInterface());
	}
}
