package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConvert extends BlockLogCommand {
	public CommandConvert(BlockLog plugin) {
		super(plugin);
	}
	
	public boolean execute(Player player, Command cmd, ArrayList<String> listArgs) {
		String[] args = (String[]) listArgs.toArray();
		if(args.length > 0) {
			player.sendMessage(ChatColor.WHITE + "/bl convert");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		if(DatabaseSettings.DBType().equalsIgnoreCase("mysql")) {
			log.info("Converting from MySQL to SQLite");
			if(MySQLToSQLite()) {
				log.info("Succesfully converted the database");
				getConfig().set("database.type", "SQLite");
				saveConfig();
				log.info("Please restart the server");
			} else
				log.severe("An error has occured while converting the database.");
		} else if(DatabaseSettings.DBType().equalsIgnoreCase("sqlite")) {
			log.info("Converting from SQLite to MySQL");
			if(SQLiteToMySQL()) {
				log.info("Succesfully converted the database");
				getConfig().set("database.type", "MySQL");
				saveConfig();
				log.info("Please restart the server");
			} else
				log.severe("An error has occured while converting the database.");
		} else
			log.info("Incorrect DB Type: " + DatabaseSettings.DBType());
		
		return true;
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
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_undos.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_reports.sql"));
			
			ResultSet BlocksRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_blocks");
			
			while(BlocksRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_blocks (player,world,block_id,datavalue,type,rollback_id,x,y,z,date) VALUES ('%s', '%s', %s, %s, %s, %s, %s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("block_id"), BlocksRS.getInt("datavalue"), BlocksRS.getInt("type"), BlocksRS.getInt("rollback_id"), BlocksRS.getInt("x"), BlocksRS.getInt("y"), BlocksRS.getInt("z"), BlocksRS.getInt("date")));
			}
			
			ResultSet InteractionsRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_interactions");
			
			while(InteractionsRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_interactions (player, world, date, x, y, z, type) VALUES ('%s', '%s', %s, %s, %s, %s, %s)", InteractionsRS.getString("player"), InteractionsRS.getString("world"), InteractionsRS.getInt("date"), InteractionsRS.getInt("x"), InteractionsRS.getInt("y"), InteractionsRS.getInt("z"), InteractionsRS.getInt("type")));
			}

			ResultSet RollbacksRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_rollbacks");
			
			while(RollbacksRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_rollbacks (player,world,date,type) VALUES ('%s', '%s', %s, %s)", RollbacksRS.getString("player"), RollbacksRS.getString("world"), RollbacksRS.getInt("date"), RollbacksRS.getInt("type")));
			}
			
			ResultSet UndosRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_undos;");
			
			while(UndosRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_undos (rollback_id,player,date) VALUES (%s, '%s', %s)", RollbacksRS.getString("rollback_id"), RollbacksRS.getString("player"), RollbacksRS.getInt("date")));
			}
			
			ResultSet ReportsRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_reports;");
			
			while(ReportsRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_reports (player,message,seen) VALUES ('%s', '%s', %s)", ReportsRS.getString("player"), ReportsRS.getString("message"), ReportsRS.getInt("seen")));
			}
			
			MySQLStmt.executeUpdate("TRUNCATE blocklog_blocks");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_interactions");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_rollbacks");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_undos");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_reports");
			
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
			MySQLStmt.executeUpdate(plugin.getResourceContent("MySQL/blocklog_undos.sql"));
			MySQLStmt.executeUpdate(plugin.getResourceContent("MySQL/blocklog_reports.sql"));
			
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
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_rollbacks (player,world,date,type) VALUES ('%s', '%s', %s, %s)", RollbacksRS.getString("player"), RollbacksRS.getString("world"), RollbacksRS.getInt("date"), RollbacksRS.getInt("type")));
			}
			
			ResultSet UndosRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_undos;");
			
			while(UndosRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_undos (rollback_id,player,date) VALUES (%s, '%s', %s)", RollbacksRS.getString("rollback_id"), RollbacksRS.getString("player"), RollbacksRS.getInt("date")));
			}
			
			ResultSet ReportsRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_reports;");
			
			while(ReportsRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_reports (player,message,seen) VALUES ('%s', '%s', %s)", ReportsRS.getString("player"), ReportsRS.getString("message"), ReportsRS.getInt("seen")));
			}
			
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_blocks");
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_interactions");
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_rollbacks");
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_undos");
			SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_reports");
			
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_blocks.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_interactions.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_undos.sql"));
			SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_reports.sql"));
			
			MySQLConn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
