package me.arno.blocklog.schedules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;
import me.arno.blocklog.logs.LoggedBlock;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class Rollback implements Runnable {
	final private BlockLog plugin;
	final private Connection conn;
	final private Player player;
	final private String target;
	final private Integer rollbackID;
	final private ResultSet blocks;
	
	public Rollback(BlockLog plugin, Player player, String target, Integer rollbackID, ResultSet blocks) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		
		this.player = player;
		this.target = target;
		this.blocks = blocks;
		this.rollbackID = rollbackID;
	}
	
	@Override
	public void run() {
		try {
			Statement rollbackStmt = conn.createStatement();
			
			Integer BlockCount = 0;
			
			World world = player.getWorld();
			
			while(blocks.next()) {
				Player logAuthor = plugin.getServer().getPlayer(blocks.getString("player"));
				
				boolean correctPlayer = (target == null);
				
				if(logAuthor != null) {
					if(logAuthor.getName().equalsIgnoreCase(target)) {
						correctPlayer = true;
					}
				}
				
				if(correctPlayer) {
					Location loc = new Location(world, blocks.getDouble("x"), blocks.getDouble("y"), blocks.getDouble("z"));
				
					LoggedBlock LBlock = new LoggedBlock(plugin, logAuthor, new MaterialData(blocks.getInt("block_id"), blocks.getByte("datavalue")), loc, blocks.getInt("type"));
					
					if(LBlock.getType() == Log.BREAK || LBlock.getType() == Log.FIRE || LBlock.getType() == Log.EXPLOSION || LBlock.getType() == Log.LEAVES)
						world.getBlockAt(LBlock.getLocation()).setTypeIdAndData(LBlock.getBlockId(), LBlock.getDataValue(), false);
					else
						world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
					
					rollbackStmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", rollbackID, blocks.getInt("id")));
					BlockCount++;
				}
			}

			
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + BlockCount + ChatColor.GOLD + " blocks changed!");
			player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/blundo " + rollbackID + ChatColor.GOLD + " to undo this rollback!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
