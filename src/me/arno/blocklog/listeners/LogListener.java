package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;

public class LogListener implements Listener {
	BlockLog plugin;
	
	Logger log;
	
	String user;
	String pass;
	String url;
	
	public LogListener(BlockLog plugin) {
		this.plugin = plugin;
		
		log = plugin.log;
		
		user = plugin.user;
		pass = plugin.pass;
		url = plugin.url;
	}
	
	public void BlocksLimitReached() {
		if(plugin.blocks.size() == plugin.getConfig().getInt("database.warning")) {
			plugin.sendAdminMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + plugin.getConfig().getInt("database.warning") + "!");
			plugin.sendAdminMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "You can fix this by using the command " + ChatColor.DARK_BLUE + "/blocklog fullsave" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/blocklog save <blocks>");
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			LoggedBlock lb = new LoggedBlock(event.getPlayer(), event.getBlockPlaced(), 1);
			plugin.blocks.add(lb);
			BlocksLimitReached();
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			LoggedBlock lb = new LoggedBlock(event.getPlayer(), event.getBlock(), 0);
			plugin.blocks.add(lb);
			BlocksLimitReached();
		}
	}
}
