package me.arno.blocklog.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandQueue extends BlockLogCommand {
	public CommandQueue() {
		super("blocklog.queue", true);
		setCommandUsage("/bl queue");
	}

	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		if(args.length > 0)
			return false;
		
		if(!hasPermission(player)) {
			player.sendMessage("You don't have permission");
			return true;
		}
		
		if(player == null) {
			log.info(String.format("The queue contains %s block edits", getQueueManager().getEditQueueSize()));
			log.info(String.format("The queue contains %s block interactions", getQueueManager().getInteractionQueueSize()));
		} else {
			player.sendMessage(String.format(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "The queue contains %s block edits", getQueueManager().getEditQueueSize()));
			player.sendMessage(String.format(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "The queue contains %s block interactions", getQueueManager().getInteractionQueueSize()));
		}
		return true;
	}
}
