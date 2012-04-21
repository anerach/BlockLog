package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandSearch extends BlockLogCommand {
	public CommandSearch(BlockLog plugin) {
		super(plugin, "blocklog.save");
	}

	public boolean execute(Player player, Command cmd, ArrayList<String> listArgs) {
		String[] args = (String[]) listArgs.toArray();
		if(args.length != 1) {
			player.sendMessage(ChatColor.WHITE + "/bl search <player>");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet actions;
			
			Integer limit = plugin.getConfig().getInt("blocklog.results");
			
			if(DatabaseSettings.DBType().equalsIgnoreCase("mysql"))
				actions = stmt.executeQuery("SELECT *, FROM_UNIXTIME(date, '%d-%m-%Y %H:%i:%s') AS fdate FROM blocklog_blocks WHERE player = '" + args[0] + "' ORDER BY date DESC LIMIT " + limit);
			else
				actions = stmt.executeQuery("SELECT *, datetime(date, 'unixepoch', 'localtime') AS fdate FROM blocklog_blocks WHERE player = '" + args[0] + "' ORDER BY date DESC LIMIT " + limit);
			
			while(actions.next()) {
				String name = Material.getMaterial(actions.getInt("block_id")).toString();
				int type = actions.getInt("type");
				
				player.sendMessage(ChatColor.BLUE + "[" + actions.getString("fdate") + "]" + ChatColor.DARK_RED + "[World:" + actions.getString("world") + ", X:" + actions.getString("x") + ", Y:" + actions.getString("y") + ", Z:" + actions.getString("z") + "]");
				if(type == 0) {
					player.sendMessage(ChatColor.GOLD + actions.getString("player") + ChatColor.DARK_GREEN + " broke a " + ChatColor.GOLD + name);
				} else if(type == 1) {
					player.sendMessage(ChatColor.GOLD + actions.getString("player") + ChatColor.DARK_GREEN + " placed a " + ChatColor.GOLD + name);
				}
			}
		} catch(SQLException e) {
			
		}
		return true;
	}

}
