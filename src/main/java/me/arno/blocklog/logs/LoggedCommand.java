package me.arno.blocklog.logs;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import me.arno.blocklog.BlockLog;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class LoggedCommand {
	private final BlockLog plugin;
	private final Player player;
	private final String message;
	private final Command cmd;
	private final Integer time;
	
	public LoggedCommand(BlockLog plugin, Player player, String message) {
		this.plugin = plugin;
		this.player = player;
		this.message = message.replace("\\", "\\\\").replace("'", "\\'").trim();
		String[] args = message.replace('/', ' ').trim().split(" ");
		this.cmd = Bukkit.getPluginCommand(args[0]);
		this.time = (int) (System.currentTimeMillis()/1000);
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
	
	public Integer getDate() {
		return time;
	}
}
