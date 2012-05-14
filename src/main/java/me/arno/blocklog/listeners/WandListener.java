package me.arno.blocklog.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.InteractionType;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.logs.BlockEdit;
import me.arno.blocklog.logs.BlockInteraction;
import me.arno.util.Query;
import me.arno.util.Text;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener extends BlockLogListener {
	public WandListener(BlockLog plugin) {
		super(plugin);
	}
	
	public void getBlockInteractions(Player player, Location location, InteractionType interaction) {
		try {
			player.sendMessage(ChatColor.YELLOW + "Block History" + ChatColor.BLUE + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" + ChatColor.DARK_GRAY + " ------------------------");
            player.sendMessage(ChatColor.GRAY + Text.addSpaces("Name", 90) + Text.addSpaces("Action", 75) + "Details");
            
			ArrayList<BlockInteraction> Interactions = getQueueManager().getInteractionQueue();
			int blockNumber = 0;
			int blockCount = 0;
			int blockSize = Interactions.size();
			int maxResults = getSettingsManager().getMaxResults();
			
			while(blockSize > blockNumber) {
				BlockInteraction LInteraction = Interactions.get(blockNumber); 
				if(LInteraction.getX() == location.getX() && LInteraction.getY() == location.getY() && LInteraction.getZ() == location.getZ() && LInteraction.getWorld() == location.getWorld()) {
					if(blockCount == maxResults)
						break;
					
					String action = "";
					if(interaction == InteractionType.CHEST || interaction == InteractionType.DISPENSER || interaction == InteractionType.DOOR || interaction == InteractionType.TRAP_DOOR)
						action = "OPENED";
					else
						action = "USED";
					
					String name = interaction.name();
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LInteraction.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + LInteraction.getPlayerName(), 99) + Text.addSpaces(ChatColor.DARK_RED + action, 99) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + date + "]");
					blockCount++;
				}
				blockNumber++;
			}
			
			if(blockCount < maxResults) {
				Query query = new Query("blocklog_interactions");
				query.select("player");
				query.selectDate("date");
				query.where("x", location.getBlockX());
				query.where("y", location.getBlockY());
				query.where("z", location.getBlockZ());
				query.where("world", location.getWorld().getName());
				query.orderBy("date", "DESC");
				query.limit(maxResults - blockCount);
				
				ResultSet rs = query.getResult();
				log.info(query.getRowCount().toString());
				
				while(rs.next()) {
	            	String action = "";
	            	if(interaction == InteractionType.CHEST || interaction == InteractionType.DISPENSER || interaction == InteractionType.DOOR || interaction == InteractionType.TRAP_DOOR)
						action = "OPENED";
					else
						action = "USED";
	            	
	            	String name = interaction.name();
					player.sendMessage(ChatColor.GOLD + Text.addSpaces(rs.getString("player"), 99) + ChatColor.DARK_RED + Text.addSpaces(action, 80) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + rs.getString("date") + "]");
				}
			}
		} catch(SQLException e) {
			e.getStackTrace();
		} catch(Exception e) {
			e.getStackTrace();
		}
	}
	
	public void getBlockEdits(Player player, Location location) {
		try {
			player.sendMessage(ChatColor.YELLOW + "Block History" + ChatColor.BLUE + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" + ChatColor.DARK_GRAY + " ------------------------");
            player.sendMessage(ChatColor.GRAY + Text.addSpaces("Name", 90) + Text.addSpaces("Action", 75) + "Details");
            
            int blockNumber = 0;
            int blockCount = 0;
			int blockSize = getQueueManager().getEditQueueSize();
			int maxResults = getSettingsManager().getMaxResults();
			
			while(blockSize > blockNumber) {
				BlockEdit LBlock = getQueueManager().getEditQueue().get(blockNumber); 
				if(LBlock.getX() == location.getX() && LBlock.getY() == location.getY() && LBlock.getZ() == location.getZ() && LBlock.getWorld() == location.getWorld()) {
					if(blockCount == maxResults)
						break;
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LBlock.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
					
					String name = Material.getMaterial(LBlock.getBlockId()).toString();
					LogType type = LBlock.getType();
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + LBlock.getPlayerName(), 99) + Text.addSpaces(ChatColor.DARK_RED + type.name(), 80) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + date + "]");
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
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getType() == getSettingsManager().getWand()  && WandEnabled) {
				if((event.getAction() == Action.RIGHT_CLICK_BLOCK && (!event.getPlayer().getItemInHand().getType().isBlock()) || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
					
					Material type = event.getClickedBlock().getType();
					if(type == Material.WOODEN_DOOR || type == Material.TRAP_DOOR || type == Material.CHEST || type == Material.DISPENSER || type == Material.STONE_BUTTON || type == Material.LEVER)
						getBlockInteractions(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.getByMaterial(type));
					else
						getBlockEdits(event.getPlayer(), event.getClickedBlock().getLocation());
					
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getType() == getSettingsManager().getWand() && WandEnabled) {
				if(event.getPlayer().getItemInHand().getType().isBlock()) {
					getBlockEdits(event.getPlayer(), event.getBlockPlaced().getLocation());
					event.setCancelled(true);
				}
			}
		}
	}
}
