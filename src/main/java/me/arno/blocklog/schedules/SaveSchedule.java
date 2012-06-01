package me.arno.blocklog.schedules;

import java.sql.SQLException;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.managers.QueueManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveSchedule implements Runnable {
	private final BlockLog plugin;

	private final CommandSender sender;
	private final Integer count;
	private final Boolean messages;

	public SaveSchedule(Integer count, CommandSender sender) {
		this(count, sender, true);
	}

	public SaveSchedule(Integer count, CommandSender sender, Boolean messages) {
		this.plugin = BlockLog.plugin;
		this.count = count;
		this.sender = sender;
		this.messages = messages;
	}
	
	@Override
	public void run() {
		try {
			if(plugin.conn == null)
				plugin.conn = plugin.getDatabaseManager().getConnection();
			
			if(plugin.conn.isClosed())
				plugin.conn = plugin.getDatabaseManager().getConnection();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(!plugin.saving || count == 1) {
			plugin.saving = true;
			if(messages) {
				if(sender != null) sender.sendMessage(ChatColor.DARK_RED + "[BlockLog]" + ChatColor.GOLD + " Saving " + ((count == 0) ? "all the" : count) + " block edits");
			}
	
			if(count == 0) {
				while(!getQueueManager().getInteractionQueue().isEmpty()) {
					getQueueManager().saveQueuedInteraction();
				}
				while(!getQueueManager().getEditQueue().isEmpty()) {
					getQueueManager().saveQueuedEdit();
				}
			} else {
				for(int i = count; i != 0; i--) {
					if (getQueueManager().getInteractionQueue().isEmpty())
						break;
					getQueueManager().saveQueuedInteraction();
				}
				for(int i = count; i != 0; i--) {
					if (getQueueManager().getEditQueue().isEmpty())
						break;
					getQueueManager().saveQueuedEdit();
				}
			}
	
			plugin.saving = false;
	
			if(messages) {
				if(sender != null) sender.sendMessage(ChatColor.DARK_RED + "[BlockLog]" + ChatColor.GOLD + " Successfully saved " + ((count == 0) ? "all the" : count) + " block edits");
			}
		}
	}
	
	private QueueManager getQueueManager() {
		return BlockLog.plugin.getQueueManager();
	}
}
