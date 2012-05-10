package me.arno.blocklog.schedules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LogType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class UndoRollback implements Runnable {
	final private BlockLog plugin;
	final private Connection conn;
	final private Player player;
	final private Integer rollbackID;
	final private ResultSet blocks;
	final private Integer limit;
	
	private Integer BlockCount = 0;	
	private Integer sid;
	
	public UndoRollback(BlockLog plugin, Player player, Integer rollbackID, ResultSet blocks, Integer limit) {
		this.plugin = plugin;
		this.conn = plugin.conn;
		this.player = player;
		this.rollbackID = rollbackID;
		this.blocks = blocks;
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
					LogType type = LogType.values()[blocks.getInt("type")];
					
					if(type == LogType.BREAK || type == LogType.FIRE || type == LogType.EXPLOSION || type == LogType.LEAVES || type == LogType.FADE || type == LogType.EXPLOSION_CREEPER || type == LogType.EXPLOSION_FIREBALL || type == LogType.EXPLOSION_TNT)
						world.getBlockAt(location).setType(Material.AIR);
					else
						world.getBlockAt(location).setTypeIdAndData(blocks.getInt("block_id"), blocks.getByte("datavalue"), false);
					
					BlockCount++;
				} else {
					rollbackStmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = 0 WHERE rollback_id = %s", rollbackID));
					
					player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + BlockCount + ChatColor.GOLD + " blocks changed!");
					player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Successfully undid rollback " + ChatColor.GREEN + "#" + rollbackID);
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
