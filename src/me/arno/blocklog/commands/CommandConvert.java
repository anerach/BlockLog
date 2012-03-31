package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Config;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandConvert implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	DatabaseSettings dbSettings;
	
	public CommandConvert(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	public boolean MySQLToSQLite() {
		try {
			Connection MySQLConn = plugin.conn;
			Connection SQLiteConn = DatabaseSettings.getConnection("sqlite");

			if(MySQLConn == null || SQLiteConn == null)
				return false;
				
			Statement MySQLStmt = MySQLConn.createStatement();
			Statement SQLiteStmt = SQLiteConn.createStatement();
			
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_blocks.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_interactions.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_rollbacks.sql"));
			
			ResultSet BlocksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_blocks");
			
			while(BlocksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_blocks (player,world,block_id,datavalue,type,rollback_id,x,y,z,date) VALUES ('%s', '%s', %s, %s, %s, %s, %s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("block_id"), BlocksRS.getInt("datavalue"), BlocksRS.getInt("type"), BlocksRS.getInt("rollback_id"), BlocksRS.getInt("x"), BlocksRS.getInt("y"), BlocksRS.getInt("z"), BlocksRS.getInt("date")));
			}
			
			ResultSet InteractionsRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_interactions");
			
			while(InteractionsRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_interactions (player, world, date, x, y, z, type) VALUES ('%s', '%s', %s, %s, %s, %s, %s)", InteractionsRS.getString("player"), InteractionsRS.getString("world"), InteractionsRS.getInt("date"), InteractionsRS.getInt("x"), InteractionsRS.getInt("y"), InteractionsRS.getInt("z"), InteractionsRS.getInt("type")));
			}

			ResultSet RollbacksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_rollbacks");
			
			while(RollbacksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_rollbacks (player,world,date,type) VALUES ('%s', '%s', %s, %s)", RollbacksRS.getString("player"), RollbacksRS.getString("world"), RollbacksRS.getInt("type"), RollbacksRS.getInt("date")));
			}
			
			MySQLStmt.executeUpdate("TRUNCATE blocklog_blocks");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_interactions");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_rollbacks");
			
			SQLiteConn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean SQLiteToMySQL() {
		try {
			Connection SQLiteConn = plugin.conn;
			Connection MySQLConn = DatabaseSettings.getConnection("mysql");
			
			if(MySQLConn == null || SQLiteConn == null)
				return false;
			
			Statement SQLiteStmt = SQLiteConn.createStatement();
			Statement MySQLStmt = MySQLConn.createStatement();
			
			MySQLStmt.executeUpdate(plugin.getResourceContent("MySQL/blocklog_blocks.sql"));
			MySQLStmt.executeUpdate(plugin.getResourceContent("MySQL/blocklog_interactions.sql"));
			MySQLStmt.executeUpdate(plugin.getResourceContent("MySQL/blocklog_rollbacks.sql"));
			
			ResultSet BlocksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_blocks;");
			
			while(BlocksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_blocks (player,world,block_id,datavalue,type,rollback_id,x,y,z,date) VALUES ('%s', '%s', %s, %s, %s, %s, %s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("block_id"), BlocksRS.getInt("datavalue"), BlocksRS.getInt("type"), BlocksRS.getInt("rollback_id"), BlocksRS.getInt("x"), BlocksRS.getInt("y"), BlocksRS.getInt("z"), BlocksRS.getInt("date")));
			}
			
			ResultSet InteractionsRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_interactions;");
			
			while(InteractionsRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_interactions (player, world, date, x, y, z, type) VALUES ('%s', '%s', %s, %s, %s, %s, %s)", InteractionsRS.getString("player"), InteractionsRS.getString("world"), InteractionsRS.getString("date"), InteractionsRS.getString("x"), InteractionsRS.getString("y"), InteractionsRS.getString("z"), InteractionsRS.getString("type")));
			}
			
			ResultSet RollbacksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_rollbacks;");
			
			while(RollbacksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_rollbacks (player,world,date,type) VALUES ('%s', '%s', %s, %s)", RollbacksRS.getString("player"), RollbacksRS.getString("world"), RollbacksRS.getInt("type"), RollbacksRS.getInt("date")));
			}
			
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_blocks");
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_interactions");
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_rollbacks");
			
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_blocks.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_interactions.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_rollbacks.sql"));
			
			MySQLConn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("blconvert"))
			return false;
		
		if(args.length > 0)
			return false;
		
		if(DatabaseSettings.DBType().equalsIgnoreCase("mysql")) {
			log.info("Converting from MySQL to SQLite");
			if(MySQLToSQLite()) {
				log.info("Succesfully converted the database");
				Config cfg = new Config();
				cfg.getConfig().set("database.type", "SQLite");
				cfg.saveConfig();
				log.info("Please restart the server");
			} else
				log.severe("An error has occured while converting the database.");
		} else if(DatabaseSettings.DBType().equalsIgnoreCase("sqlite")) {
			log.info("Converting from SQLite to MySQL");
			if(SQLiteToMySQL()) {
				log.info("Succesfully converted the database");
				Config cfg = new Config();
				cfg.getConfig().set("database.type", "MySQL");
				cfg.saveConfig();
				log.info("Please restart the server");
			} else
				log.severe("An error has occured while converting the database.");
		} else
			log.info("Incorrect DB Type: " + DatabaseSettings.DBType());
		
		return true;
	}
}
