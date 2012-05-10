package me.arno.blocklog.schedules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.database.Query;
import me.arno.blocklog.logs.LogType;

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
	private final Query query;
	private final Integer limit;
	
	private Integer BlockCount = 0;	
	private Integer sid;
	
	public Rollback(BlockLog plugin, Player player, Integer rollbackID, Query query, Integer limit) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		this.player = player;
		this.rollbackID = rollbackID;
		this.query = query;
		this.limit = limit;
	}
	
	public void setId(Integer sid) {
		this.sid = sid;
	}
	
	@Override
	public void run() {
		try {
			Statement rollbackStmt = conn.createStatement();
			ResultSet blocks = query.getResult();
			
			World world = player.getWorld();
			
			for(int i=0;i<limit;i++) {
				if(blocks.next()) {
					Location location = new Location(world, blocks.getDouble("x"), blocks.getDouble("y"), blocks.getDouble("z"));
					LogType type = LogType.values()[blocks.getInt("type")];
					
					if(type == LogType.BREAK || type == LogType.FIRE || type == LogType.EXPLOSION || type == LogType.LEAVES || type == LogType.FADE || type == LogType.EXPLOSION_CREEPER || type == LogType.EXPLOSION_FIREBALL || type == LogType.EXPLOSION_TNT)
						world.getBlockAt(location).setTypeIdAndData(blocks.getInt("block_id"), blocks.getByte("datavalue"), false);
					else
						world.getBlockAt(location).setType(Material.AIR);
					
					rollbackStmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", rollbackID, blocks.getInt("id")));
					BlockCount++;
				} else {
					player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + BlockCount + ChatColor.GOLD + " blocks of the " + ChatColor.GOLD + query.getRowCount() + " blocks changed!");
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
