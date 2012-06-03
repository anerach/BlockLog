package me.arno.blocklog.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Text;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener extends BlockLogListener {
	
	public void getBlockEdits(Player player, Location location) {
		try {
			player.sendMessage(ChatColor.YELLOW + "Block History" + ChatColor.BLUE + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" + ChatColor.DARK_GRAY + " ------------------------");
            player.sendMessage(ChatColor.GRAY + Text.addSpaces("Name", 90) + Text.addSpaces("Action", 75) + "Details");
            
            int blockNumber = 0;
            int blockCount = 0;
			int blockSize = getQueueManager().getEditQueueSize();
			int maxResults = getSettingsManager().getMaxResults();
			
			while(blockSize > blockNumber) {
				BlockEntry LBlock = getQueueManager().getEditQueue().get(blockNumber); 
				if(LBlock.getX() == location.getX() && LBlock.getY() == location.getY() && LBlock.getZ() == location.getZ() && LBlock.getWorld().equalsIgnoreCase(location.getWorld().getName())) {
					if(blockCount == maxResults)
						break;
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LBlock.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
					
					String name = Material.getMaterial(LBlock.getBlock()).toString();
					LogType type = LBlock.getType();
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + LBlock.getPlayer(), 99) + Text.addSpaces(ChatColor.DARK_RED + type.name(), 80) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + date + "]");
					blockCount++;
				}
				blockNumber++;
			}
			
			
			if(blockCount < maxResults) {
				Query query = new Query("blocklog_blocks");
				query.select("entity", "triggered", "block_id", "type");
				query.selectDate("date");
				query.where("x", location.getBlockX());
				query.where("y", location.getBlockY());
				query.where("z", location.getBlockZ());
				query.where("world", location.getWorld().getName());
				query.orderBy("date", "DESC");
				query.limit(maxResults - blockCount);
				
				ResultSet rs = query.getResult();
				
				while(rs.next()) {
					String name = Material.getMaterial(rs.getInt("block_id")).toString();
					LogType type = LogType.values()[rs.getInt("type")];
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + rs.getString("triggered"), 99) + Text.addSpaces(ChatColor.DARK_RED + type.name(), 81) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + rs.getString("date") + "]");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		
	}
}
