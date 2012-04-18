package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;

public class LoggedDeath {
	private final BlockLog plugin;
	private final Player player;
	private final Integer type;
	private final Long time;
	
	public LoggedDeath(BlockLog plugin, Player player, Integer type) {
		this.plugin = plugin;
		this.player = player;
		this.type = type;
		this.time = System.currentTimeMillis()/1000;
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_deaths (player, type, date) VALUES ('" + getPlayerName() + "', '" + getType() + "', " + time + ")");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getPlayerName() {
		return player.getName();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Integer getType() {
		return type;
	}
}
