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
	final private Boolean force;
	
	private Boolean saving;
	
	public Save(BlockLog plugin, int count, Player player, boolean force) {
		this.blocks = plugin.blocks;
		this.interactions = plugin.interactions;
		this.log = plugin.log;
		this.saving = plugin.saving;
		
		this.count = count;
		this.player = player;
		this.force = force;
	}
	
	@Override
	public void run() {
		if((force == false && saving == true) && player != null) {
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "We're already saving some of the blocks.");
		} else if(force == false && saving == true && player == null) {
			log.info("We're already saving some of the blocks.");
		} else if(force == true || saving == false) {
			saving = true;
			if(player == null)
				log.info("Saving " + ((count == 0) ? "all the" : count) + " block edits!");
			else
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving " + ((count == 0) ? "all the" : count) + " block edits!");
			
			if(count == 0) {
	    		while(interactions.size() > 0) {
	    			LoggedInteraction interaction = interactions.get(0);
			    	interaction.save();
			    	interactions.remove(0);
	    		}
				while(blocks.size() > 0) {
		    		LoggedBlock block = blocks.get(0);
				    block.save();
				    blocks.remove(0);
	    		}
	    	} else {
	    		if(interactions.size() > 0) {
	    			for(int i=count; i!=0; i--) {
	    				LoggedInteraction interaction = interactions.get(0);
			    		interaction.save();
				    	interactions.remove(0);
	    			}
	    		}
	    		if(blocks.size() > 0) {
	    			for(int i=count; i!=0; i--) {
		    			LoggedBlock block = blocks.get(0);
				    	block.save();
				    	blocks.remove(0);
	    			}
	    		}
	    	}
			
			if(player == null)
				log.info("Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
			else
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + ((count == 0) ? "all the" : count) + " block edits!");
			
			saving = false;
		}
	}
}
