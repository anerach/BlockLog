package me.arno.blocklog.listeners;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {
BlockLog plugin;
	
	Logger log;
	
	String user;
	String pass;
	String url;
	
	public WandListener(BlockLog plugin) {
		this.plugin = plugin;
		
		log = plugin.log;
		
		user = plugin.user;
		pass = plugin.pass;
		url = plugin.url;
	}
	
	public void getBlockEdits(Player player, Block block) {
		try {
			player.sendMessage(ChatColor.DARK_RED + "BlockLog History (" + plugin.getConfig().getString("blocklog.results") + " Last Edits)");
			int BlockNumber = 0;
			int BlockCount = 0;
			int BlockSize = plugin.blocks.size();
			Location BlockLocation = block.getLocation();
			while(BlockSize > BlockNumber)
			{
				LoggedBlock LBlock = plugin.blocks.get(BlockNumber); 
				if(LBlock.getX() == BlockLocation.getX() && LBlock.getY() == BlockLocation.getY() && LBlock.getZ() == BlockLocation.getZ()) {
					if(BlockCount == plugin.getConfig().getInt("blocklog.results"))
						break;
					
					String str = (LBlock.getType() == 1) ? "placed a" : "broke a";
					String name = Material.getMaterial(LBlock.getBlockId()).toString();
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LBlock.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
					
					player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayer() + " " + ChatColor.DARK_GREEN + str + " " + ChatColor.GOLD + name);
					BlockCount++;
				}
				BlockNumber++;
			}
			if(BlockCount < plugin.getConfig().getInt("blocklog.results")) {
				Connection conn = plugin.getConnection();
				Statement stmt = conn.createStatement();
				
				double x = block.getX();
				double y = block.getY();
				double z = block.getZ();
				
				ResultSet rs;
				if(plugin.getConfig().getBoolean("mysql.enabled"))
					rs = stmt.executeQuery("SELECT player, block_id, type, FROM_UNIXTIME(date, '%d-%m-%Y %H:%i:%s') AS date FROM blocklog WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' ORDER BY date DESC LIMIT " + (plugin.getConfig().getInt("blocklog.results") - BlockCount));
				else
					rs = stmt.executeQuery("SELECT player, block_id, type, datetime(date, 'unixepoch', 'localtime') AS date FROM blocklog WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' ORDER BY date DESC LIMIT " + (plugin.getConfig().getInt("blocklog.results") - BlockCount));
				
				while(rs.next()) {
					String str = (rs.getInt("type") == 1) ? "placed a" : "broke a";
					String name = Material.getMaterial(rs.getInt("block_id")).toString();
					
					player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + " " + ChatColor.DARK_GREEN + str + " " + ChatColor.GOLD + name);
				}
			}
		} catch(SQLException e) {
			log.info("[BlockLog][Wand][Interact][SQL] Exception!");
			log.info("[BlockLog][Wand][Interact][SQL] " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getType().getId() == plugin.getConfig().getInt("blocklog.wand")  && plugin.users.contains(event.getPlayer().getName()) && ((event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().getItemInHand().getType().isBlock()) || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
				getBlockEdits(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getType().getId() == plugin.getConfig().getInt("blocklog.wand") && plugin.users.contains(event.getPlayer().getName()) && event.getPlayer().getItemInHand().getType().isBlock()) {
				getBlockEdits(event.getPlayer(), event.getBlockPlaced());
				event.setCancelled(true);
			}
		}
	}
}
