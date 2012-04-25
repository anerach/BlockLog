package me.arno.blocklog.schedules;

import java.util.ArrayList;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LoggedBlock;
import me.arno.blocklog.logs.LoggedInteraction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Save implements Runnable {
	final private ArrayList<LoggedBlock> blocks;
	final private ArrayList<LoggedInteraction> interactions;
	final private Logger log;
	
	final private Player player;
	final private Integer count;
	final private Boolean messages;
	
	public Save(BlockLog plugin, Integer count, Player player) {
		this(plugin, count, player, true);
	}
	
	public Save(BlockLog plugin, Integer count, Player player, Boolean messages) {
		this.blocks = plugin.getBlocks();
		this.interactions = plugin.getInteractions();
		this.log = plugin.log;
		
		this.count = count;
		this.player = player;
		this.messages = messages;
	}
	
	@Override
	public void run() {
		if(messages) {
			if(player == null)
				log.info("Saving " + ((count == 0) ? "all the" : count) + " block edits!");
			else
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving " + ((count == 0) ? "all the" : count) + " block edits!");
		}
		
		if(count == 0) {
	    	while(!interactions.isEmpty()) {
	    		try {
	    			interactions.get(0).save();
			    	interactions.remove(0);
		    	} catch(Exception e) {}
	    	}
	    	log.info("Start Blocks");
			while(!blocks.isEmpty()) {
				try {
			   		blocks.get(0).save();
				    blocks.remove(0);
				} catch(Exception e) {}
	    	}
			log.info("End Blocks");
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
		
		if(messages) {
			if(player == null)
				log.info("Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
			else
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
		}	
	}
}
