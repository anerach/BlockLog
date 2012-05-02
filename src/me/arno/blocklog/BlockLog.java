package me.arno.blocklog;

import java.io.BufferedWriter;
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

import me.arno.blocklog.commands.CommandAutoSave;
import me.arno.blocklog.commands.CommandCancel;
import me.arno.blocklog.commands.CommandClear;
import me.arno.blocklog.commands.CommandConfig;
import me.arno.blocklog.commands.CommandHelp;
import me.arno.blocklog.commands.CommandLookup;
import me.arno.blocklog.commands.CommandRead;
import me.arno.blocklog.commands.CommandReload;
import me.arno.blocklog.commands.CommandReport;
import me.arno.blocklog.commands.CommandRollback;
import me.arno.blocklog.commands.CommandRollbackList;
import me.arno.blocklog.commands.CommandSave;
import me.arno.blocklog.commands.CommandSearch;
import me.arno.blocklog.commands.CommandStorage;
import me.arno.blocklog.commands.CommandUndo;
import me.arno.blocklog.commands.CommandWand;
import me.arno.blocklog.database.DatabaseSettings;
import me.arno.blocklog.listeners.BlockListener;
import me.arno.blocklog.listeners.InteractionListener;
import me.arno.blocklog.listeners.McMMOListener;
import me.arno.blocklog.listeners.NoticeListener;
import me.arno.blocklog.listeners.PlayerListener;
import me.arno.blocklog.listeners.WandListener;
import me.arno.blocklog.logs.LoggedBlock;
import me.arno.blocklog.logs.LoggedInteraction;
import me.arno.blocklog.schedules.Save;
import me.arno.blocklog.schedules.Updates;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
	
	public Config logConfig;
	
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
	
	public FileConfiguration getLogConfig() {
		return logConfig.getConfig();
	}
	
	public void saveLogConfig() {
		logConfig.saveConfig();
	}
	
	private void loadConfiguration() {
		ArrayList<String> worlds = new ArrayList<String>();
		for(World world : getServer().getWorlds()) {
			worlds.add(world.getName());
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
	    getConfig().addDefault("blocklog.worlds", worlds);
	    getConfig().addDefault("blocklog.reports", true);
	    getConfig().addDefault("blocklog.updates", true);
	    getConfig().addDefault("blocklog.dateformat", "%d-%m-%Y %H:%i:%s");
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
		
	    getLogConfig().addDefault("logs.grow", true);
	    getLogConfig().addDefault("logs.leaves", true);
	    getLogConfig().addDefault("logs.portal", false);
	    getLogConfig().addDefault("logs.form", false);
	    getLogConfig().addDefault("logs.fade", false);
	    getLogConfig().addDefault("logs.spread", false);
	    getLogConfig().addDefault("logs.chat", false);
	    getLogConfig().addDefault("logs.kill", false);
	    getLogConfig().addDefault("logs.death", false);
	    getLogConfig().options().copyDefaults(true);
	    saveLogConfig();
		
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
	    		stmt.executeUpdate(getResourceContent("MySQL/blocklog_" + table + ".sql"));
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
		logConfig = new Config("logging.yml");
		log = getLogger();
		
		log.info("Loading metrics");
		loadMetrics();
	    
	    log.info("Loading the configurations");
	    loadConfiguration();
	    
	    log.info("Loading the database");
	    loadDatabase();
	    updateDatabase();
	    
	    log.info("Cleaning up the database");
	    CleanUpDatabase();
	    
	    log.info("Loading the dependencies");
	    loadDependencies();
	    
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
		
		if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
			CommandHelp command = new CommandHelp(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("autosave")) {
			CommandAutoSave command = new CommandAutoSave(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("cancel")) {
			CommandCancel command = new CommandCancel(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("clear")) {
			CommandClear command = new CommandClear(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg")) {
			CommandConfig command = new CommandConfig(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("lookup")) {
			CommandLookup command = new CommandLookup(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("read")) {
			CommandRead command = new CommandRead(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("reload")) {
			CommandReload command = new CommandReload(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("report")) {
			CommandReport command = new CommandReport(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("rollback") || args[0].equalsIgnoreCase("rb")) {
			CommandRollback command = new CommandRollback(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("rollbacklist") || args[0].equalsIgnoreCase("rblist") || args[0].equalsIgnoreCase("rbl")) {
			CommandRollbackList command = new CommandRollbackList(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("save")) {
			CommandSave command = new CommandSave(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("search")) {
			CommandSearch command = new CommandSearch(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("storage")) {
			CommandStorage command = new CommandStorage(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("undo")) {
			CommandUndo command = new CommandUndo(this);
			return command.execute(player, cmd, newArgs);
		} else if(args[0].equalsIgnoreCase("wand")) {
			CommandWand command = new CommandWand(this);
			return command.execute(player, cmd, newArgs);
		}
		return true;
	}
}
