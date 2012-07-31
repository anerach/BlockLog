package me.arno.blocklog.schedules;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.managers.QueueManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveSchedule implements Runnable {
	private final BlockLog plugin;

	private final CommandSender sender;
	private final int count;
	private final boolean messages;

	public SaveSchedule(int count, CommandSender sender) {
		this(count, sender, true);
	}

	public SaveSchedule(int count, CommandSender sender, boolean messages) {
		this.plugin = BlockLog.getInstance();
		this.count = count;
		this.sender = sender;
		this.messages = messages;
	}
	
	private QueueManager getQueueManager() {
		return BlockLog.getInstance().getQueueManager();
	}
	
	@Override
	public void run() {
		if(!plugin.saving || count == 1) {
			plugin.saving = true;
			
			if(messages && sender != null) sender.sendMessage(ChatColor.DARK_RED + "[BlockLog]" + ChatColor.GOLD + " Saving " + ((count == 0) ? "all the" : count) + " block edits");
	
			if(count == 0) {
				while(!getQueueManager().isQueueEmpty()) {
					getQueueManager().saveQueue();
				}
			} else {
				for(int i = count; i != 0; i--) {
					if(getQueueManager().isQueueEmpty())
						break;
					getQueueManager().saveQueue();
				}
			}
	
			plugin.saving = false;
	
			if(messages && sender != null) sender.sendMessage(ChatColor.DARK_RED + "[BlockLog]" + ChatColor.GOLD + " Successfully saved " + ((count == 0) ? "all the" : count) + " block edits");
		}
	}
}
