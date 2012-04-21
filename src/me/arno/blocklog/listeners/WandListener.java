package me.arno.blocklog.listeners;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Interaction;
import me.arno.blocklog.database.DatabaseSettings;
import me.arno.blocklog.logs.LoggedBlock;
import me.arno.blocklog.logs.LoggedInteraction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener extends BlockLogListener {
	public WandListener(BlockLog plugin) {
		super(plugin);
	}
	
	public void getBlockInteractions(Player player, Block block, Interaction interaction) {
		try {
			player.sendMessage(ChatColor.DARK_RED + "BlockLog History (" + getConfig().getString("blocklog.results") + " Last Edits)");
			
			ArrayList<LoggedInteraction> Interactions = plugin.interactions;
			int BlockNumber = 0;
			int BlockCount = 0;
			int BlockSize = Interactions.size();
			Location BlockLocation = block.getLocation();
			
			while(BlockSize > BlockNumber)
			{
				LoggedInteraction LInteraction = Interactions.get(BlockNumber); 
				if(LInteraction.getX() == BlockLocation.getX() && LInteraction.getY() == BlockLocation.getY() && LInteraction.getZ() == BlockLocation.getZ() && LInteraction.getWorld() == BlockLocation.getWorld()) {
					if(BlockCount == getConfig().getInt("blocklog.results"))
						break;
					
					String str = "";
					if(interaction == Interaction.CHEST || interaction == Interaction.DISPENSER)
						str = "opened this";
					else
						str = "used this";
					
					String name = interaction.getMaterial().name();
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LInteraction.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
					
					player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LInteraction.getPlayerName() + " " + ChatColor.DARK_GREEN + str + " " + ChatColor.GOLD + name);
					BlockCount++;
				}
				BlockNumber++;
			}
			
			if(BlockCount < getConfig().getInt("blocklog.results")) {
				Connection conn = plugin.conn;
				Statement stmt = conn.createStatement();
				
				double x = BlockLocation.getX();
				double y = BlockLocation.getY();
				double z = BlockLocation.getZ();
				
				ResultSet rs;
				if(DatabaseSettings.DBType().equalsIgnoreCase("mysql"))
					rs = stmt.executeQuery("SELECT player, FROM_UNIXTIME(date, '%d-%m-%Y %H:%i:%s') AS date FROM blocklog_interactions WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' AND world = '" + BlockLocation.getWorld().getName() + "' ORDER BY date DESC LIMIT " + (getConfig().getInt("blocklog.results") - BlockCount));
				else
					rs = stmt.executeQuery("SELECT player, datetime(date, 'unixepoch', 'localtime') AS date FROM blocklog_interactions WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' AND world = '" + BlockLocation.getWorld().getName() + "' ORDER BY date DESC LIMIT " + (getConfig().getInt("blocklog.results") - BlockCount));
				
				while(rs.next()) {
					String str = "";
					if(interaction == Interaction.CHEST || interaction == Interaction.DISPENSER || interaction == Interaction.DOOR || interaction == Interaction.TRAP_DOOR)
						str = "opened a";
					else
						str = "used a";
					
					String name = interaction.getMaterial().name();
					
					player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + " " + ChatColor.DARK_GREEN + str + " " + ChatColor.GOLD + name);
				}
			}
		} catch(SQLException e) {
			e.getStackTrace();
		}
	}
	
	public void getBlockEdits(Player player, Block block) {
		try {
			player.sendMessage(ChatColor.DARK_RED + "BlockLog History (" + getConfig().getString("blocklog.results") + " Last Edits)");
			int BlockNumber = 0;
			int BlockCount = 0;
			int BlockSize = plugin.blocks.size();
			Location BlockLocation = block.getLocation();
			while(BlockSize > BlockNumber)
			{
				LoggedBlock LBlock = plugin.blocks.get(BlockNumber); 
				if(LBlock.getX() == BlockLocation.getX() && LBlock.getY() == BlockLocation.getY() && LBlock.getZ() == BlockLocation.getZ() && LBlock.getWorld() == BlockLocation.getWorld()) {
					if(BlockCount == getConfig().getInt("blocklog.results"))
						break;
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LBlock.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
					
					String name = Material.getMaterial(LBlock.getBlockId()).toString();
					int type = LBlock.getTypeId();
					
					if(type == 0)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayerName() + ChatColor.DARK_GREEN + " broke a " + ChatColor.GOLD + name);
					else if(type == 1)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayerName() + ChatColor.DARK_GREEN + " placed a " + ChatColor.GOLD + name);
					else if(type == 2)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayerName() + ChatColor.DARK_GREEN + " burned a " + ChatColor.GOLD + name);
					else if(type == 3 || type == 10 || type == 11 || type == 12)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayerName() + ChatColor.DARK_GREEN + " blew a " + ChatColor.GOLD + name + ChatColor.DARK_GREEN + " up");
					else if(type == 4)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.DARK_GREEN + "A " + ChatColor.GOLD + name + ChatColor.DARK_GREEN + " decayed");
					else if(type == 5)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayerName() + ChatColor.DARK_GREEN + " grew a " + ChatColor.GOLD + name);
					else if(type == 6 || type == 7 || type == 8)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.GOLD + LBlock.getPlayerName() + ChatColor.DARK_GREEN + " created a " + ChatColor.GOLD + name);
					else if(type == 9)
						player.sendMessage(ChatColor.BLUE + "[" + date + "] " + ChatColor.DARK_GREEN + "A " + ChatColor.GOLD + name + ChatColor.DARK_GREEN + " faded");
					BlockCount++;
				}
				BlockNumber++;
			}
			
			if(BlockCount < getConfig().getInt("blocklog.results")) {
				Connection conn = plugin.conn;
				Statement stmt = conn.createStatement();
				
				double x = block.getX();
				double y = block.getY();
				double z = block.getZ();
				
				ResultSet rs;
				if(DatabaseSettings.DBType().equalsIgnoreCase("mysql")) {
					rs = stmt.executeQuery("SELECT player, block_id, type, FROM_UNIXTIME(date, '%d-%m-%Y %H:%i:%s') AS date FROM blocklog_blocks WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' AND world = '" + block.getWorld().getName() + "' ORDER BY date DESC LIMIT " + (getConfig().getInt("blocklog.results") - BlockCount));
				} else {
					rs = stmt.executeQuery("SELECT player, block_id, type, datetime(date, 'unixepoch', 'localtime') AS date FROM blocklog_blocks WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' AND world = '" + block.getWorld().getName() + "' ORDER BY date DESC LIMIT " + (getConfig().getInt("blocklog.results") - BlockCount));
				}
				
				while(rs.next()) {
					String name = Material.getMaterial(rs.getInt("block_id")).toString();
					int type = rs.getInt("type");
					
					if(type == 0)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + ChatColor.DARK_GREEN + " broke a " + ChatColor.GOLD + name);
					else if(type == 1)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + ChatColor.DARK_GREEN + " placed a " + ChatColor.GOLD + name);
					else if(type == 2)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + ChatColor.DARK_GREEN + " burned a " + ChatColor.GOLD + name);
					else if(type == 3 || type == 10 || type == 11 || type == 12)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + ChatColor.DARK_GREEN + " blew a " + ChatColor.GOLD + name + ChatColor.DARK_GREEN + " up");
					else if(type == 4)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.DARK_GREEN + "A " + ChatColor.GOLD + name + ChatColor.DARK_GREEN + " decayed");
					else if(type == 5)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + ChatColor.DARK_GREEN + " grew a " + ChatColor.GOLD + name);
					else if(type == 6 || type == 7 || type == 8)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + ChatColor.DARK_GREEN + " created a " + ChatColor.GOLD + name);
					else if(type == 9)
						player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.DARK_GREEN + "A " + ChatColor.GOLD + name + ChatColor.DARK_GREEN + " faded");
				}
			}
		} catch(SQLException e) {
			e.getStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		int BLWand = getConfig().getInt("blocklog.wand");
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getTypeId() == BLWand  && WandEnabled) {
				if((event.getAction() == Action.RIGHT_CLICK_BLOCK && (!event.getPlayer().getItemInHand().getType().isBlock()) || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
					Material type = event.getClickedBlock().getType();
					if(type == Material.WOODEN_DOOR || type == Material.TRAP_DOOR || type == Material.CHEST || type == Material.DISPENSER || type == Material.STONE_BUTTON || type == Material.LEVER)
						getBlockInteractions(event.getPlayer(), event.getClickedBlock(), Interaction.getByMaterial(type));
					else
						getBlockEdits(event.getPlayer(), event.getClickedBlock());
					
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		int BLWand = getConfig().getInt("blocklog.wand");
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getTypeId() == BLWand && WandEnabled) {
				if(event.getPlayer().getItemInHand().getType().isBlock()) {
					getBlockEdits(event.getPlayer(), event.getBlockPlaced());
					event.setCancelled(true);
				}
			}
		}
	}
}
