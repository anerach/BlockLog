package me.arno.blocklog.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

public class DatabaseManager {
	public String getHost() {
		return BlockLog.plugin.getConfig().getString("mysql.host");
	}
	
	public String getUsername() {
		return BlockLog.plugin.getConfig().getString("mysql.username");
	}
	
	public String getPassword() {
		return BlockLog.plugin.getConfig().getString("mysql.password");
	}
	
	public String getDatabase() {
		return BlockLog.plugin.getConfig().getString("mysql.database");
	}
	
	public int getPort() {
		return BlockLog.plugin.getConfig().getInt("mysql.port");
	}
	
	public Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			return DriverManager.getConnection("jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase(), getUsername(), getPassword());
		} catch (InstantiationException ex) {
		} catch (IllegalAccessException ex) {
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
			throw new SQLException("Unable to find the MySQL JDBC Driver");
		}
		return null;
	}
}
