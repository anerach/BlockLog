package me.arno.blocklog.database;

import java.sql.*;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

public class ConvertDatabase {
	String DBType; // MySQL, SQLite, H2
	Logger log;
	BlockLog plugin;
	
	public ConvertDatabase(BlockLog plugin) {
		this.plugin = plugin;
		this.DBType = plugin.cfg.getConfig().getString("database.type");
		if(DBType == "MySQL" || DBType == "SQLite") {
			if(DBType == "MySQL")
				MySQLToSQLite();
			else if(DBType == "SQLite")
				SQLiteToMySQL();
		} else
			log.info("Incorrect DB Type: " + DBType);
	}
	
	public void MySQLToSQLite() {
		try {
			Connection MySQLConn = plugin.conn;
			Connection SQLiteConn = DatabaseSettings.getConnection("sqlite");

			Statement MySQLStmt = MySQLConn.createStatement();
			Statement SQLiteStmt = SQLiteConn.createStatement();
			
			ResultSet BlocksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_blocks");
			ResultSet RollbacksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_rollbacks");
			
			MySQLStmt.executeUpdate("TRUNCATE blocklog_blocks");
			MySQLStmt.executeUpdate("TRUNCATE blocklog_rollbacks");
			
			while(BlocksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_blocks (player,world,block_id,type,rollback_id,x,y,z,date) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("block_id"), BlocksRS.getInt("type"), BlocksRS.getInt("rollback_id"), BlocksRS.getInt("x"), BlocksRS.getInt("y"), BlocksRS.getInt("z"), BlocksRS.getInt("date")));
			}
			
			while(RollbacksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_rollbacks (player,world,date,type) VALUES (%s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("type"), BlocksRS.getInt("date")));
			}
			SQLiteConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void SQLiteToMySQL() {
		try {
			Connection SQLiteConn = plugin.conn;
			Connection MySQLConn = DatabaseSettings.getConnection("mysql");
			
			Statement SQLiteStmt = SQLiteConn.createStatement();
			Statement MySQLStmt = MySQLConn.createStatement();
			
			ResultSet BlocksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_blocks");
			ResultSet RollbacksRS = SQLiteStmt.executeQuery("SELECT * FROM blocklog_rollbacks");

			SQLiteStmt.executeUpdate("TRUNCATE blocklog_blocks");
			SQLiteStmt.executeUpdate("TRUNCATE blocklog_rollbacks");
			
			while(BlocksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_blocks (player,world,block_id,type,rollback_id,x,y,z,date) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("block_id"), BlocksRS.getInt("type"), BlocksRS.getInt("rollback_id"), BlocksRS.getInt("x"), BlocksRS.getInt("y"), BlocksRS.getInt("z"), BlocksRS.getInt("date")));
			}
			
			while(RollbacksRS.next()) {
				MySQLStmt.executeUpdate(String.format("INSERT INTO blocklog_rollbacks (player,world,date,type) VALUES (%s, %s, %s, %s)", BlocksRS.getString("player"), BlocksRS.getString("world"), BlocksRS.getInt("type"), BlocksRS.getInt("date")));
			}
			MySQLConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
