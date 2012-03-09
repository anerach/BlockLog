package me.arno.blocklog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockLogListener implements Listener {
	BlockLog plugin;
	
	Logger log;
	
	String user;
	String pass;
	String url;
	
	List<String> users = new ArrayList<String>();
	
	public BlockLogListener(BlockLog plugin) {
		this.plugin = plugin;
		
		log = plugin.log;
		
		user = plugin.user;
		pass = plugin.pass;
		url = plugin.url;
		
		users = plugin.users;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getPlayer().getItemInHand().getType() == Material.BLAZE_ROD  && users.contains(event.getPlayer().getName()) && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
			try {
				Connection conn = plugin.getConnection();
				Statement stmt = conn.createStatement();
				
				Player player = event.getPlayer();
				
				Block block = event.getClickedBlock();
				
				double x = block.getX();
				double y = block.getY();
				double z = block.getZ();
				
				ResultSet rs;
				if(plugin.getConfig().getBoolean("mysql.enabled"))
					rs = stmt.executeQuery("SELECT player, block_id, type, FROM_UNIXTIME('%d-%m-%Y %H:%M:%s', date) AS date FROM blocklog WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' ORDER BY date DESC LIMIT " + plugin.getConfig().getInt("blocklog.results"));
				else
					rs = stmt.executeQuery("SELECT player, block_id, type, datetime(date, 'unixepoch', 'localtime') AS date FROM blocklog WHERE x = '" + x + "' AND y = '" + y + "' AND z = '" + z + "' ORDER BY date DESC LIMIT " + plugin.getConfig().getInt("blocklog.results"));
				
				player.sendMessage(ChatColor.DARK_RED + "BlockLog History (" + plugin.getConfig().getString("blocklog.results") + " Last Edits)");
				
				while(rs.next()) {
					String str = (rs.getInt("type") == 1) ? "placed a" : "broke a";
					
					String name = Material.getMaterial(rs.getInt("block_id")).toString();
					
					player.sendMessage(ChatColor.BLUE + "[" + rs.getString("date") + "] " + ChatColor.GOLD + rs.getString("player") + " " + ChatColor.DARK_GREEN + str + " " + ChatColor.GOLD + name);
				}
			} catch(SQLException ex) {
				log.info("[BlockLog][Player][Interact][SQL] Exception!");
				log.info("[BlockLog][Player][Interact][SQL] " + ex.getMessage());
			} catch (Exception ex) {
				log.info("[BlockLog][Player][Interact] Exception!");
				log.info("[BlockLog][Player][Interact] " + ex.getMessage());
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			Block block = event.getBlockPlaced();
			int id = block.getType().getId();
			String player = event.getPlayer().getName();
			
			try {
				Connection conn = plugin.getConnection();
				Statement stmt = conn.createStatement();
				
				double x = block.getX();
				double y = block.getY();
				double z = block.getZ();
				
				if(plugin.getConfig().getBoolean("mysql.enabled"))
					stmt.executeUpdate("INSERT INTO blocklog (player, block_id, date, x, y, z, type) VALUES ('" + player + "', " + id + ", UNIX_TIMESTAMP(), " + x + ", " + y + ", " + z + ", 1)");
				else
					stmt.executeUpdate("INSERT INTO blocklog (player, block_id, date, x, y, z, type) VALUES ('" + player + "', " + id + ", strftime('%s', 'now'), " + x + ", " + y + ", " + z + ", 1)");
			} catch(SQLException ex) {
				log.info("[BlockLog][Block][Place][SQL] Exception!");
				log.info("[BlockLog][Block][Place][SQL] " + ex.getMessage());
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			Block block = event.getBlock();
			int id = block.getType().getId();
			String player = event.getPlayer().getName();
			
			try {
				Connection conn = plugin.getConnection();
				Statement stmt = conn.createStatement();
				
				double x = block.getX();
				double y = block.getY();
				double z = block.getZ();
				if(plugin.getConfig().getBoolean("mysql.enabled"))
					stmt.executeUpdate("INSERT INTO blocklog (player, block_id, date, x, y, z, type) VALUES ('" + player + "', " + id + ", UNIX_TIMESTAMP(), " + x + ", " + y + ", " + z + ", 0)");
				else
					stmt.executeUpdate("INSERT INTO blocklog (player, block_id, date, x, y, z, type) VALUES ('" + player + "', " + id + ", strftime('%s', 'now'), " + x + ", " + y + ", " + z + ", 0)");
			} catch(SQLException ex) {
				log.info("[BlockLog][Block][Break][SQL] Exception!");
				log.info("[BlockLog][Block][Break][SQL] " + ex.getMessage());
			}
		}
	}
}
