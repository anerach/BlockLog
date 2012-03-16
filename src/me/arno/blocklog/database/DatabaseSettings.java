package me.arno.blocklog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

public class DatabaseSettings {
	public static Connection getConnection(BlockLog plugin) {
		try {
			return getConnection(plugin, "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Connection getConnection(BlockLog plugin, String type) throws SQLException
	{
		try {
			String DBType = plugin.getConfig().getString("database.type");
			String MySQLHost = plugin.getConfig().getString("mysql.host");
			String MySQLUser = plugin.getConfig().getString("mysql.username");
			String MySQLPass = plugin.getConfig().getString("mysql.password");
			String MySQLDatabase = plugin.getConfig().getString("mysql.database");
			int MySQLPort = plugin.getConfig().getInt("mysql.port");
			
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String DBType(BlockLog plugin) {
		return plugin.getConfig().getString("database.type");
	}
}
