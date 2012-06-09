package me.arno.blocklog.commands;

import me.arno.blocklog.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandQueue extends BlockLogCommand {
	public CommandQueue() {
		super("blocklog.queue", true);
		setCommandUsage("/bl queue");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 0)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		sender.sendMessage(String.format(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "The queue contains %s block edits", getQueueManager().getEditQueueSize()));
		
		sender.sendMessage(ChatColor.YELLOW + "BlockLog Queue" + ChatColor.DARK_GRAY + " -------------------------------");
		sender.sendMessage(ChatColor.GRAY + Util.addSpaces("Queue", 100) + "Amount");
		sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Blocks", 109) + getQueueManager().getEditQueueSize());
		sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Chests", 109) + getQueueManager().getChestQueueSize());
		sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Data", 109) + getQueueManager().getDataQueueSize());
		sender.sendMessage(Util.addSpaces(ChatColor.GOLD + "Interactions", 109) + getQueueManager().getInteractionQueueSize());
		
		return true;
	}
}
