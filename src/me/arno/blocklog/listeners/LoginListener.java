package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
	BlockLog plugin;
	
	Logger log;
	
	public LoginListener(BlockLog plugin) {
		this.plugin = plugin;
		
		log = plugin.log;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
	    if ((player.isOp() || player.hasPermission("blocklog.notices")) && plugin.NewVersion != null) {
	    	player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "There is a new version of BlockLog available (v" + plugin.NewVersion + ")");
	    }
	}
}
