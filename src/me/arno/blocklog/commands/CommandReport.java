package me.arno.blocklog.commands;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandReport extends BlockLogCommand {
	public CommandReport(BlockLog plugin) {
		super(plugin, "blocklog.report.write");
	}

	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length < 1) {
			player.sendMessage(ChatColor.WHITE + "/bl report <message>");
			return true;
		}
		
		if(!plugin.getConfig().getBoolean("blocklog.reports")) {
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "The report system is disabled");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		String msg = "";
		
		for(int i = 0; i < args.length;i++)
			msg += ((i == 0) ? "" : " ") + args[i];
		
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO blocklog_reports (player, message, seen) VALUES (?, ?, 0)");
			stmt.setString(1, player.getName());
			stmt.setString(2, msg);
			stmt.executeUpdate();
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Your report has been created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
