package me.arno.blocklog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

public class DatabaseSettings {
	BlockLog plugin;
	
	private boolean MySQLEnabled;
	private String MySQLHost;
	private String MySQLUser;
	private String MySQLPass;
	private String MySQLDatabase;
	private int MySQLPort;
	private String MySQLUrl;
	
	private String SQLiteUrl;
	
	public DatabaseSettings(BlockLog plugin) {
		this.plugin = plugin;
		
		MySQLEnabled = plugin.getConfig().getBoolean("mysql.enabled");
		MySQLHost = plugin.getConfig().getString("mysql.host");
		MySQLUser = plugin.getConfig().getString("mysql.username");
		MySQLPass = plugin.getConfig().getString("mysql.password");
		MySQLDatabase = plugin.getConfig().getString("mysql.database");
		MySQLPort = plugin.getConfig().getInt("mysql.port");
		
		MySQLUrl = "jdbc:mysql://" + MySQLHost + ":" + MySQLPort + "/" + MySQLDatabase;
		SQLiteUrl = "jdbc:sqlite:plugins/BlockLog/blocklog.db";
	}
	
	public Connection getConnection() throws SQLException
	{
		try {
			if(MySQLEnabled) {
				return DriverManager.getConnection(MySQLUrl, MySQLUser, MySQLPass);
			} else {
				Class.forName("org.sqlite.JDBC");
				return DriverManager.getConnection(SQLiteUrl);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean MySQLEnabled() {
		return MySQLEnabled;
	}
}
