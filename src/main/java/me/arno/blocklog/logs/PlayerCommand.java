package me.arno.blocklog.logs;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class PlayerCommand {
	private final BlockLog plugin;
	private final Player player;
	private final String message;
	private final String[] args;
	private final Command cmd;
	private final float time;
	
	public PlayerCommand(Player player, String message) {
		this.plugin = BlockLog.plugin;
		this.player = player;
		this.message = message.replace("\\", "\\\\").replace("'", "\\'").trim();
		this.args = message.replace('/', ' ').trim().split(" ");
		this.cmd = Bukkit.getPluginCommand(args[0]);
		this.time = System.currentTimeMillis()/1000;
	}
	
	public void save() {
		try {
			PreparedStatement stmt = plugin.conn.prepareStatement("INSERT INTO blocklog_commands (player, command, date) VALUES ('" + getPlayerName() + "', '" + getMessage() + "', " + getDate() + ")");
			stmt.executeUpdate();
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
	
	public String getCommandName() {
		return cmd.getName();
	}
	
	public Command getCommand() {
		return cmd;
	}
	
	public String getMessage() {
		return message;
	}
	
	public float getDate() {
		return time;
	}
}
