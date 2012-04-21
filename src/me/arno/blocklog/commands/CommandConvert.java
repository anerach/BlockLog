package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandConvert extends BlockLogCommand {
	public CommandConvert(BlockLog plugin) {
		super(plugin, true);
	}
	
	public boolean execute(Player player, Command cmd, String[] args) {
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
			
			for(String table : plugin.tables)
				SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_" + table + ".sql"));
			
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
			
			ResultSet ChatRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_chat;");
			
			while(ChatRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_reports (player,message,date) VALUES ('%s', '%s', %s)", ChatRS.getString("player"), ChatRS.getString("message"), ChatRS.getInt("date")));
			}
			
			ResultSet DeathsRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_deaths;");
			
			while(DeathsRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_deaths (player,type,world,x,y,z,date) VALUES ('%s', '%s', '%s', %s, %s, %s, %s)", DeathsRS.getString("player"), DeathsRS.getString("type"), DeathsRS.getString("world"), DeathsRS.getInt("x"), DeathsRS.getInt("y"), DeathsRS.getInt("z"), DeathsRS.getInt("date")));
			}
			
			ResultSet KillsRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_kills;");
			
			while(KillsRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_kills (victem,killer,world,x,y,z,date) VALUES ('%s', '%s', '%s', %s, %s, %s, %s)", KillsRS.getString("victem"), KillsRS.getString("killer"), KillsRS.getString("world"), KillsRS.getInt("x"), KillsRS.getInt("y"), KillsRS.getInt("z"), KillsRS.getInt("date")));
			}
			
			ResultSet CommandsRS = MySQLStmt.executeQuery("SELECT * FROM blocklog_commands;");
			
			while(CommandsRS.next()) {
				SQLiteStmt.executeUpdate(String.format("INSERT INTO blocklog_commands (player,command,date) VALUES ('%s', '%s', %s)", CommandsRS.getString("player"), CommandsRS.getString("command"), CommandsRS.getInt("date")));
			}
			
			
			for(String table : plugin.tables)
				MySQLStmt.executeUpdate("TRUNCATE blocklog_" + table);
			
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
			
			for(String table : plugin.tables)
				MySQLStmt.executeUpdate(plugin.getResourceContent("MySQL/blocklog_" + table + ".sql"));
			
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
			
			ResultSet ChatRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_chat;");
			
			while(ChatRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_reports (player,message,date) VALUES ('%s', '%s', %s)", ChatRS.getString("player"), ChatRS.getString("message"), ChatRS.getInt("date")));
			}
			
			ResultSet DeathsRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_deaths;");
			
			while(DeathsRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_deaths (player,type,world,x,y,z,date) VALUES ('%s', '%s', '%s', %s, %s, %s, %s)", DeathsRS.getString("player"), DeathsRS.getString("type"), DeathsRS.getString("world"), DeathsRS.getInt("x"), DeathsRS.getInt("y"), DeathsRS.getInt("z"), DeathsRS.getInt("date")));
			}
			
			ResultSet KillsRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_kills;");
			
			while(KillsRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_kills (victem,killer,world,x,y,z,date) VALUES ('%s', '%s', '%s', %s, %s, %s, %s)", KillsRS.getString("victem"), KillsRS.getString("killer"), KillsRS.getString("world"), KillsRS.getInt("x"), KillsRS.getInt("y"), KillsRS.getInt("z"), KillsRS.getInt("date")));
			}
			
			ResultSet CommandsRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_commands;");
			
			while(CommandsRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_commands (player,command,date) VALUES ('%s', '%s', %s)", CommandsRS.getString("player"), CommandsRS.getString("command"), CommandsRS.getInt("date")));
			}
			
			for(String table : plugin.tables) {
				SQLiteStmt.executeUpdate("DROP TABLE IF EXISTS blocklog_" + table);
				SQLiteStmt.executeUpdate(plugin.getResourceContent("SQLite/blocklog_" + table + ".sql"));
			}
			
			MySQLConn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
