package me.arno.blocklog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.arno.blocklog.Config;

public class DatabaseSettings {
	
	public static Connection getConnection() {
		return getConnection(new Config().getConfig().getString("database.type"));
	}
	
	public static Connection getConnection(String type)
	{
		try {
			Config cfg = new Config();
			String DBType = cfg.getConfig().getString("database.type");
			String MySQLHost = cfg.getConfig().getString("mysql.host");
			String MySQLUser = cfg.getConfig().getString("mysql.username");
			String MySQLPass = cfg.getConfig().getString("mysql.password");
			String MySQLDatabase = cfg.getConfig().getString("mysql.database");
			int MySQLPort = cfg.getConfig().getInt("mysql.port");
			
			String MySQLUrl = "jdbc:mysql://" + MySQLHost + ":" + MySQLPort + "/" + MySQLDatabase;
			String SQLiteUrl = "jdbc:sqlite:plugins/BlockLog/blocklog.db";
			
			if(DBType.equalsIgnoreCase("mysql") || type.equalsIgnoreCase("mysql")) {
				Connection conn = DriverManager.getConnection(MySQLUrl, MySQLUser, MySQLPass);
				return conn;
			} else if(DBType.equalsIgnoreCase("sqlite") || type.equalsIgnoreCase("sqlite")) {
				Class.forName("org.sqlite.JDBC");
				Connection conn =  DriverManager.getConnection(SQLiteUrl);
				return conn;
			} else
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String DBType() {
		return new Config().getConfig().getString("database.type");
	}
}
