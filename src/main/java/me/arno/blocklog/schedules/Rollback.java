package me.arno.blocklog.schedules;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.BlockEdit;
import me.arno.blocklog.util.Query;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Rollback implements Runnable {
	private final BlockLog plugin;
	private final Player player;
	private final int rollbackID;
	private final int limit;
	private final int totalBlocks;
	
	private final ArrayList<BlockEdit> blockEdits = new ArrayList<BlockEdit>();
	
	private int blockCount = 0;	
	private int sid;
	
	public Rollback(Player player, int rollbackID, Query query, int limit) throws SQLException {
		this.plugin = BlockLog.plugin;
		this.player = player;
		this.rollbackID = rollbackID;
		this.limit = limit;
		this.totalBlocks = query.getRowCount();
		
		ResultSet rs = query.getResult();
		
		while(rs.next()) {
			int id = rs.getInt("id");
			String entity = rs.getString("entity");
			String triggered = rs.getString("triggered");
			int block_id = rs.getInt("block_id");
			int data = rs.getInt("datavalue");
			int gamemode = rs.getInt("gamemode");
			int type = rs.getInt("type");
			long date = rs.getLong("date");
			

			String world = rs.getString("world");
			int x = rs.getInt("x");
			int y = rs.getInt("y");
			int z = rs.getInt("z");
			Location location = new Location(Bukkit.getWorld(world), x, y, z);
			
			BlockEdit blockEdit = new BlockEdit(id, triggered, entity, block_id, data, gamemode, location, type, date);
			blockEdit.setRollback(rs.getInt("rollback_id"));
			
			blockEdits.add(blockEdit);
		}
	}
	
	public void setId(Integer sid) {
		this.sid = sid;
	}
	
	@Override
	public void run() {
		for(int i=0;i<limit;i++) {
			if(blockEdits.size() > 0) {
				BlockEdit blockEdit = blockEdits.get(0);
				if(blockEdit.rollback(rollbackID))
					blockCount++;
				
				blockEdits.remove(0);
			} else {
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + blockCount + ChatColor.GOLD + " blocks of the " + ChatColor.GREEN + totalBlocks + ChatColor.GOLD + " blocks changed!");
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/bl undo " + rollbackID + ChatColor.GOLD + " to undo this rollback!");
				plugin.getSchedules().remove(sid);
				plugin.getServer().getScheduler().cancelTask(sid);
				break;
			}
		}
	}

}
