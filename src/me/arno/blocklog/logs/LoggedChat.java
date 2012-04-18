package me.arno.blocklog.logs;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;

public class LoggedChat {
	private final BlockLog plugin;
	private final Player player;
	private final String message;
	private final Long time;
	
	public LoggedChat(BlockLog plugin, Player player, String message) {
		this.plugin = plugin;
		this.player = player;
		this.message = message.replace("\'", "\\'");
		this.time = System.currentTimeMillis()/1000;
	}
	
	public void save() {
		try {
			PreparedStatement stmt = plugin.conn.prepareStatement("INSERT INTO blocklog_chat (player, message, date) VALUES ('" + getPlayerName() + "', '" + getMessage() + "', " + time + ")");
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getPlayerName() {
		return getPlayer().getName();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getMessage() {
		return message;
	}
}
