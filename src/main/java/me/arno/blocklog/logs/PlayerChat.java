package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;

public class PlayerChat {
	private final BlockLog plugin;
	private final Player player;
	private final String message;
	private final Long time;
	
	public PlayerChat(Player player, String message) {
		this.plugin = BlockLog.plugin;
		this.player = player;
		this.message = message.replace("\\", "\\\\").replace("'", "\\'").trim();
		this.time = System.currentTimeMillis()/1000;
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_chat (player, message, date) VALUES ('" + getPlayerName() + "', '" + getMessage() + "', " + time + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getPlayerName() {
		return getPlayer().getName().toLowerCase();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getMessage() {
		return message;
	}
}
