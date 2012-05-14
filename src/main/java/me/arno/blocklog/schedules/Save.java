package me.arno.blocklog.schedules;

import java.sql.SQLException;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.managers.QueueManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Save implements Runnable {
	private final BlockLog plugin;

	private final CommandSender sender;
	private final Integer count;
	private final Boolean messages;

	public Save(Integer count, CommandSender sender) {
		this(count, sender, true);
	}

	public Save(Integer count, CommandSender sender, Boolean messages) {
		this.plugin = BlockLog.plugin;
		this.count = count;
		this.sender = sender;
		this.messages = messages;
	}

	@Override
	public void run() {
		try {
			if(!plugin.conn.isClosed()) {
				plugin.conn = plugin.getDatabaseManager().getConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(messages) {
			if(sender == null) sender.sendMessage(ChatColor.GOLD + "Saving " + ((count == 0) ? "all the" : count) + " block edits");
		}

		if(count == 0) {
			while(!getQueueManager().getInteractionQueue().isEmpty()) {
				try {
					getQueueManager().saveQueuedInteraction();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			while(!getQueueManager().getEditQueue().isEmpty()) {
				try {
					getQueueManager().saveQueuedEdit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for(int i = count; i != 0; i--) {
				try {
					if (getQueueManager().getInteractionQueue().isEmpty())
						break;
					getQueueManager().saveQueuedInteraction();
				} catch (Exception e) {
				}
			}
			for(int i = count; i != 0; i--) {
				try {
					if (getQueueManager().getEditQueue().isEmpty())
						break;
					getQueueManager().saveQueuedEdit();
				} catch (Exception e) {
				}
			}
		}

		plugin.saving = false;

		if(messages) {
			if(sender == null) sender.sendMessage(ChatColor.GOLD + "Successfully saved " + ((count == 0) ? "all the" : count) + " block edits");
		}
	}
	
	private QueueManager getQueueManager() {
		return BlockLog.plugin.getQueueManager();
	}
}
