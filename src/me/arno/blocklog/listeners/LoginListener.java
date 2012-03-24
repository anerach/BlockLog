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
		if ((player.isOp() || player.hasPermission("blocklog.notices")) && plugin.newVersion != null) {
			player.sendMessage(ChatColor.BLUE + "BlockLog " + ChatColor.GOLD + "v" + plugin.newVersion + ChatColor.BLUE + " is released! You're using BlockLog " + ChatColor.GOLD + "v" + plugin.currentVersion);
		}
	}
}
