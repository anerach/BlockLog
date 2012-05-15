package me.arno.blocklog.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class NoticeListener extends BlockLogListener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if ((player.isOp() || player.hasPermission("blocklog.notices")) && (plugin.doubleNewVersion > plugin.doubleCurrentVersion)) {
			player.sendMessage(ChatColor.BLUE + "BlockLog " + ChatColor.GOLD + "v" + plugin.newVersion + ChatColor.BLUE + " is released! You're using BlockLog " + ChatColor.GOLD + "v" + plugin.currentVersion);
		}
	}
}
