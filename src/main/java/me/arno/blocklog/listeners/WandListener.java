package me.arno.blocklog.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Interaction;
import me.arno.blocklog.Log;
import me.arno.blocklog.database.Query;
import me.arno.blocklog.logs.LoggedBlock;
import me.arno.blocklog.logs.LoggedInteraction;
import me.arno.blocklog.util.Text;

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
	
	public void getBlockInteractions(Player player, Location location, Interaction interaction) {
		try {
			player.sendMessage(ChatColor.YELLOW + "Block History" + ChatColor.BLUE + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" + ChatColor.DARK_GRAY + " ----------------------------------");
            player.sendMessage(ChatColor.GRAY + Text.addSpaces("Name", 90) + Text.addSpaces("Reason", 75) + "Details");
            
			ArrayList<LoggedInteraction> Interactions = plugin.getInteractions();
			int BlockNumber = 0;
			int BlockCount = 0;
			int BlockSize = Interactions.size();
			
			while(BlockSize > BlockNumber)
			{
				LoggedInteraction LInteraction = Interactions.get(BlockNumber); 
				if(LInteraction.getX() == location.getX() && LInteraction.getY() == location.getY() && LInteraction.getZ() == location.getZ() && LInteraction.getWorld() == location.getWorld()) {
					if(BlockCount == getConfig().getInt("blocklog.results"))
						break;
					
					String action = "";
					if(interaction == Interaction.CHEST || interaction == Interaction.DISPENSER || interaction == Interaction.DOOR || interaction == Interaction.TRAP_DOOR)
						action = "OPENED";
					else
						action = "USED";
					
					String name = interaction.getMaterial().name();
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LInteraction.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + LInteraction.getPlayerName(), 99) + Text.addSpaces(ChatColor.DARK_RED + action, 99) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + date + "]");
					BlockCount++;
				}
				BlockNumber++;
			}
			
			if(BlockCount < getConfig().getInt("blocklog.results")) {
				Query query = new Query("blocklog_interactions");
				query.addSelect("player");
				query.addSelectDateAs("date", "date");
				query.addWhere("x", location.getBlockX());
				query.addWhere("y", location.getBlockY());
				query.addWhere("z", location.getBlockZ());
				query.addWhere("world", location.getWorld().getName());
				query.addOrderBy("date", "DESC");
				query.addLimit(getConfig().getInt("blocklog.results") - BlockCount);
				
				ResultSet rs = query.getResult();
				
				while(rs.next()) {
	            	String action = "";
	            	if(interaction == Interaction.CHEST || interaction == Interaction.DISPENSER || interaction == Interaction.DOOR || interaction == Interaction.TRAP_DOOR)
						action = "OPENED";
					else
						action = "USED";
	            	String name = Material.getMaterial(rs.getInt("block_id")).toString();
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + rs.getString("date"), 99) + Text.addSpaces(ChatColor.DARK_RED + action, 80) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + rs.getString("date") + "]");
				}
			}
		} catch(SQLException e) {
			e.getStackTrace();
		}
	}
	
	public void getBlockEdits(Player player, Location location) {
		try {
			player.sendMessage(ChatColor.YELLOW + "Block History" + ChatColor.BLUE + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" + ChatColor.DARK_GRAY + " ----------------------------------");
            player.sendMessage(ChatColor.GRAY + Text.addSpaces("Name", 90) + Text.addSpaces("Reason", 75) + "Details");
            
            Integer BlockNumber = 0;
			Integer BlockCount = 0;
			Integer BlockSize = plugin.getBlocks().size();
			
			while(BlockSize > BlockNumber)
			{
				LoggedBlock LBlock = plugin.getBlocks().get(BlockNumber); 
				if(LBlock.getX() == location.getX() && LBlock.getY() == location.getY() && LBlock.getZ() == location.getZ() && LBlock.getWorld() == location.getWorld()) {
					if(BlockCount == getConfig().getInt("blocklog.results"))
						break;
					
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.setTimeInMillis(LBlock.getDate() * 1000);
					
					String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
					
					String name = Material.getMaterial(LBlock.getBlockId()).toString();
					Log type = LBlock.getType();
					
					if(type.getId() <= 12 && type.getId() >= 10)
						type = Log.EXPLOSION;
					
					player.sendMessage(Text.addSpaces(ChatColor.GOLD + LBlock.getPlayerName(), 99) + Text.addSpaces(ChatColor.DARK_RED + type.name(), 80) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + date + "]");
					BlockCount++;
				}
				BlockNumber++;
			}
			
			
			// Database Results
			Query query = new Query("blocklog_blocks");
			query.addSelect("entity", "trigered", "block_id", "type");
			query.addSelectDateAs("date", "date");
			query.addWhere("x", location.getBlockX());
			query.addWhere("y", location.getBlockY());
			query.addWhere("z", location.getBlockZ());
			query.addWhere("world", location.getWorld().getName());
			query.addOrderBy("date", "DESC");
			
			ResultSet rs = query.getResult();
			
			while(rs.next()) {
				String name = Material.getMaterial(rs.getInt("block_id")).toString();
				Log type = Log.values()[rs.getInt("type")];
				
				if(type.getId() <= 12 && type.getId() >= 10)
					type = Log.EXPLOSION;
				
				player.sendMessage(Text.addSpaces(ChatColor.GOLD + rs.getString("trigered"), 99) + Text.addSpaces(ChatColor.DARK_RED + type.name(), 81) + ChatColor.GREEN + name + ChatColor.AQUA + " [" + rs.getString("date") + "]");
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
						getBlockInteractions(event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.getByMaterial(type));
					else
						getBlockEdits(event.getPlayer(), event.getClickedBlock().getLocation());
					
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
					getBlockEdits(event.getPlayer(), event.getBlockPlaced().getLocation());
					event.setCancelled(true);
				}
			}
		}
	}
}
