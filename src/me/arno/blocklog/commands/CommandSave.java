package me.arno.blocklog.commands;

import java.util.ArrayList;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandSave extends BlockLogCommand {
	public CommandSave(BlockLog plugin) {
		super(plugin, "blocklog.save");
	}

	public boolean execute(Player player, Command cmd, ArrayList<String> listArgs) {
		String[] args = (String[]) listArgs.toArray();
		if(args.length > 1) {
			player.sendMessage(ChatColor.WHITE + "/bl save [amount|all]");
			return true;
		}
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("all")) {
			plugin.saveLogs(0, player);
		} else {
			int blockCount = 100;
			if(args.length == 1)
				blockCount = Integer.parseInt(args[0]);
				
			plugin.saveLogs(blockCount, player);
		}
		return true;
	}

}
