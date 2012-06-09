package me.arno.blocklog.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener extends BlockLogListener {
	
	public void getBlockEdits(Player player, Location location) {
		try {
			player.sendMessage(ChatColor.YELLOW + "Block History" + ChatColor.BLUE + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" + ChatColor.DARK_GRAY + " ------------------------");
            player.sendMessage(ChatColor.GRAY + Util.addSpaces("Name", 90) + Util.addSpaces("Action", 75) + "Details");
            
            int blockNumber = 0;
            int blockCount = 0;
			int blockSize = getQueueManager().getEditQueueSize();
			int maxResults = getSettingsManager().getMaxResults();
			
			while(blockSize > blockNumber) {
				BlockEntry LBlock = getQueueManager().getQueuedEdit(blockNumber); 
				if(LBlock.getX() == location.getX() && LBlock.getY() == location.getY() && LBlock.getZ() == location.getZ() && LBlock.getWorld().equalsIgnoreCase(location.getWorld().getName())) {
					if(blockCount == maxResults)
						break;
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LBlock.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
					
					String name = Material.getMaterial(LBlock.getBlock()).toString();
					LogType type = LBlock.getType();
					
					player.sendMessage(Util.addSpaces(ChatColor.GOLD + LBlock.getPlayer(), 99) + Util.addSpaces(ChatColor.DARK_RED + type.toString(), 80) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + date + "]");
					blockCount++;
				}
				blockNumber++;
			}
			
			
			if(blockCount < maxResults) {
				Query query = new Query();
				Query blockQuery = new Query("blocklog_blocks");
				Query chestQuery = new Query("blocklog_chests");
				Query interactionQuery = new Query("blocklog_interactions");
				
				blockQuery.select("player", "entity", "block", "data", "0 AS amount", "type").selectDate("date");
				chestQuery.select("player", "'player'", "item", "data", "amount", "type").selectDate("date");;
				interactionQuery.select("player", "'player'", "block", "0", "0", "19").selectDate("date");;
				
				blockQuery.where("x", location.getBlockX()).where("y", location.getBlockY()).where("z", location.getBlockZ());
				chestQuery.where("x", location.getBlockX()).where("y", location.getBlockY()).where("z", location.getBlockZ());
				interactionQuery.where("x", location.getBlockX()).where("y", location.getBlockY()).where("z", location.getBlockZ());
				
				blockQuery.where("world", location.getWorld().getName());
				chestQuery.where("world", location.getWorld().getName());
				interactionQuery.where("world", location.getWorld().getName());
				
				query.orderBy("date", "DESC");
				query.limit(maxResults - blockCount);
				
				Statement stmt = BlockLog.plugin.conn.createStatement();
				ResultSet rs = stmt.executeQuery(query.unionClause(blockQuery, chestQuery, interactionQuery));
				
				while(rs.next()) {
					String name = Material.getMaterial(rs.getInt("block")).toString() + (rs.getInt("amount") == 0 ? "" : " (" + rs.getInt("amount") + ")");
					LogType type = LogType.values()[rs.getInt("type")];
					
					player.sendMessage(Util.addSpaces(ChatColor.GOLD + rs.getString("player"), 99) + Util.addSpaces(ChatColor.DARK_RED + type.toString(), 81) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + rs.getString("date") + "]");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(plugin.users.contains(player.getName()) && player.getItemInHand().getType() == getSettingsManager().getWand()) {
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				getBlockEdits(player, event.getClickedBlock().getLocation());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if(!getSettingsManager().getWand().isBlock())
			return;
		
		if(plugin.users.contains(player.getName()) && player.getItemInHand().getType() == getSettingsManager().getWand()) {
			getBlockEdits(player, event.getBlock().getLocation());
			event.setCancelled(true);
		}
	}
}
