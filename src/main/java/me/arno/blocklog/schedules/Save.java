package me.arno.blocklog.schedules;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.BlockEdit;
import me.arno.blocklog.logs.BlockInteraction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Save implements Runnable {
	private final BlockLog plugin;
	private final ArrayList<BlockEdit> blocks;
	private final ArrayList<BlockInteraction> interactions;
	private final Logger log;
	
	private final Player player;
	private final Integer count;
	private final Boolean messages;
	
	public Save(BlockLog plugin, Integer count, Player player) {
		this(plugin, count, player, true);
	}
	
	public Save(BlockLog plugin, Integer count, Player player, Boolean messages) {
		this.plugin = plugin;
		this.blocks = plugin.getQueueManager().getEditQueue();
		this.interactions = plugin.getQueueManager().getInteractionQueue();
		this.log = plugin.log;
		
		this.count = count;
		this.player = player;
		this.messages = messages;
	}
	
	@Override
	public void run() {
		if(!plugin.saving || count == 1) {
			plugin.saving = true;
			try {
				if(plugin.conn.isValid(3)) {
					plugin.conn = plugin.getDatabaseManager().getConnection();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(messages) {
				if(player == null)
					log.info("Saving " + ((count == 0) ? "all the" : count) + " block edits");
				else
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving " + ((count == 0) ? "all the" : count) + " block edits");
			}
			
			if(count == 0) {
		    	while(!interactions.isEmpty()) {
		    		try {
		    			interactions.get(0).save();
				    	interactions.remove(0);
			    	} catch(Exception e) {}
		    	}
				while(!blocks.isEmpty()) {
					try {
				   		blocks.get(0).save();
					    blocks.remove(0);
					} catch(Exception e) {}
		    	}
		    } else {
		    	for(int i=count; i!=0; i--) {
		    		try {
		    			if(interactions.isEmpty())
		    				break;
		    			interactions.get(0).save();
					    interactions.remove(0);
			    	} catch(Exception e) {}
		    	}
		    	for(int i=count; i!=0; i--) {
		    		try {
			    		if(blocks.isEmpty())
			    			break;
			    		blocks.get(0).save();
			    		blocks.remove(0);
			    	} catch(Exception e) {}
		    	}
		    }
			
			plugin.saving = false;
			
			if(messages) {
				if(player == null)
					log.info("Successfully saved " + ((count == 0) ? "all the" : count) + " block edits");
				else
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + ((count == 0) ? "all the" : count) + " block edits");
			}
		} else {
			if(messages) {
				if(player == null)
					log.info("We're already saving the block edits");
				else
					player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "We're already saving the block edits");
			}
		}
	}
}