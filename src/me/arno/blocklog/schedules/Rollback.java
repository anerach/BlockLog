package me.arno.blocklog.schedules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Rollback implements Runnable {
	private final BlockLog plugin;
	private final Connection conn;
	private final Player player;
	private final Integer rollbackID;
	private final ResultSet blocks;
	private final Integer limit;
	
	private Integer BlockCount = 0;	
	private Integer sid;
	
	public Rollback(BlockLog plugin, Player player, Integer rollbackID, ResultSet blocks, Integer limit) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		this.player = player;
		this.blocks = blocks;
		this.rollbackID = rollbackID;
		this.limit = limit;
	}
	
	public void setId(Integer sid) {
		this.sid = sid;
	}
	
	@Override
	public void run() {
		try {
			Statement rollbackStmt = conn.createStatement();
			
			World world = player.getWorld();
			
			for(int i=0;i<limit;i++) {
				if(blocks.next()) {
					Location location = new Location(world, blocks.getDouble("x"), blocks.getDouble("y"), blocks.getDouble("z"));
					Log type = Log.values()[blocks.getInt("type")];
					
					
					if(type == Log.BREAK || type == Log.FIRE || type == Log.EXPLOSION || type == Log.LEAVES || type == Log.FADE || type == Log.EXPLOSION_CREEPER || type == Log.EXPLOSION_GHAST || type == Log.EXPLOSION_TNT)
						world.getBlockAt(location).setTypeIdAndData(blocks.getInt("block_id"), blocks.getByte("datavalue"), false);
					else
						world.getBlockAt(location).setType(Material.AIR);
					
					rollbackStmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", rollbackID, blocks.getInt("id")));
					BlockCount++;
				} else {
					player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + BlockCount + ChatColor.GOLD + " blocks changed!");
					player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/bl undo " + rollbackID + ChatColor.GOLD + " to undo this rollback!");
					plugin.getSchedules().remove(sid);
					plugin.getServer().getScheduler().cancelTask(sid);
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
