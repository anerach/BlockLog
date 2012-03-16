package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;

public class LogListener implements Listener {
	BlockLog plugin;
	
	Logger log;
	float time;
	
	public LogListener(BlockLog plugin) {
		this.plugin = plugin;
		
		log = plugin.log;
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
		log.info(msg);
	}
	
	public void BlocksLimitReached() {
		int BlockSize = plugin.blocks.size();
		if(plugin.autoSave == BlockSize && BlockSize != 0) {
			if(plugin.autoSaveMsg)
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog][AutoSave] " + ChatColor.GOLD + "Saving " + plugin.blocks.size() + " blocks!");
			
			plugin.saveBlocks(0);
			if(plugin.autoSaveMsg)
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog][AutoSave] " + ChatColor.GOLD + "Succesfully saved all the blocks!");
		} else if(BlockSize == plugin.getConfig().getInt("blocklog.warning")) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() + 30000;
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + BlockSize + "!");
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "You can fix this by using the command " + ChatColor.DARK_BLUE + "/blfullsave" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/blsave <blocks>");
			}
		} else if(BlockSize > plugin.getConfig().getInt("blocklog.warning") && BlockSize % 100 == 0) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() + 30000;
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + BlockSize + "!");
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "You can fix this by using the command " + ChatColor.DARK_BLUE + "/blfullsave" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/blsave <blocks>");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		int BLWand = plugin.getConfig().getInt("blocklog.wand");
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getTypeId() != BLWand || !WandEnabled) {
				LoggedBlock lb = new LoggedBlock(plugin.conn, event.getPlayer(), event.getBlockPlaced(), 1);
				plugin.blocks.add(lb);
				BlocksLimitReached();
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			LoggedBlock lb = new LoggedBlock(plugin.conn, event.getPlayer(), event.getBlock(), 0);
			plugin.blocks.add(lb);
			BlocksLimitReached();
		}
	}
}
