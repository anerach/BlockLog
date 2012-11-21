package me.arno.blocklog.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.managers.DatabaseManager;
import me.arno.blocklog.util.BukkitUtil;
import me.arno.blocklog.util.Query;

public class BlockSearch {
	private Connection conn;
	private String player;
	private String entity;
	
	private String world;
	private Location location;
	private int area = 0;
	
	private int rollback = 0;
	
	private int since = 0;
	private int until = 0;
	
	private int limit = 0;
	
	private boolean useLocation = false;
	private boolean groupByLocation = true;
	private boolean ignoreLimit = true;
	
	public BlockSearch() { this.conn = BlockLog.getInstance().getConnection(); }
	public BlockSearch(Connection conn) { this.conn = conn; }

	public void setPlayer(String player) {
		this.player = player;
	}
	
	public void setEntity(String entity) {
		if(entity != null) {
			if(entity.equalsIgnoreCase("tnt"))
				entity = "primed_tnt";
		}
		
		this.entity = entity;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public void setLocation(Location location) {
		this.location = location;
		if(location != null)
			setWorld(location.getWorld().getName());
	}
	
	public void setArea(int area) {
		this.area = area;
	}
	
	public void setRollback(int rollback) {
		this.rollback = rollback;
	}
	
	public void setDate(int since) {
		setDate(since, 0);
	}
	
	public void setDate(int since, int until) {
		this.since = since;
		this.until = until;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
		this.ignoreLimit = false;
	}
	
	public void setUseLocation(boolean useLocation) {
		this.useLocation = useLocation;
	}
	public void setIgnoreLimit(boolean ignore) {
		this.ignoreLimit = ignore;
	}
	
	public ArrayList<BlockEntry> getResults() {
		ArrayList<BlockEntry> blockEntries = new ArrayList<BlockEntry>();
		
		World world = null;
		int xMin = 0; int xMax = 0; int yMin = 0; int yMax = 0; int zMin = 0; int zMax = 0;
		
		if(location != null) {
			world = location.getWorld();
			
			xMin = location.getBlockX() - area;
			xMax = location.getBlockX() + area;
			yMin = location.getBlockY() - area;
			yMax = location.getBlockY() + area;
			zMin = location.getBlockZ() - area;
			zMax = location.getBlockZ() + area;
		}
		
		Query query = new Query(DatabaseManager.databasePrefix + "blocks");
		query.select("*");
		if(player != null)
			query.where("player", player);
		if(entity != null)
			query.where("entity", entity);
		if(since != 0)
			query.where("date", since, ">=");
		if(until != 0)
			query.where("date", until, "<=");
		if(location != null && area > 0)
			query.where("x", xMin, ">=").where("x", xMax, "<=").where("y", yMin, ">=").where("y", yMax, "<=").where("z", zMin, ">=").where("z", zMax, "<=");
		else if(location != null && useLocation)
			query.where("x", location.getBlockX()).where("y", location.getBlockY()).where("z", location.getBlockZ());
			
		if(world != null)
			query.where("world", world.getName());
		
		query.where("rollback", rollback);
		
		if(groupByLocation)
			query.groupBy("x", "y", "z");
		
		query.orderBy("date", "DESC");
		
		try {
			ResultSet rs = query.getResult(conn);
			
			for(BlockEntry edit : BlockLog.getInstance().getQueueManager().getBlockEntries()) {
				if(limit == 0 && !ignoreLimit)
					break;
				
				if(checkEdit(edit)) {
					blockEntries.add(edit);
					limit--;
				}
			}
			
			while(rs.next()) {
				if(limit == 0 && !ignoreLimit)
					break;
				
				int id = rs.getInt("id");
				String player = rs.getString("player");
				String entity = rs.getString("entity");
				int block = rs.getInt("block");
				byte data = rs.getByte("data");
				int originalBlock = rs.getInt("original_block");
				byte originalData = rs.getByte("original_data");
				int type = rs.getInt("type");
				int rollback = rs.getInt("rollback");
				long date = rs.getLong("date");
				
				Location loc = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));

				EntityType entityType = EntityType.valueOf(entity.toUpperCase());
				LogType logType = LogType.values()[type];
				
				BlockEntry blockEntry = new BlockEntry(player, entityType, logType, loc, block, data, originalBlock, originalData);
				blockEntry.setId(id);
				blockEntry.setRollback(rollback);
				blockEntry.setDate(date);
				
				blockEntries.add(blockEntry);
				limit--;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blockEntries;
	}
	
	public boolean checkEdit(BlockEntry entry) {
		if(world != null) {
			if(!world.equalsIgnoreCase(entry.getWorld()))
				return false;
		}
		
		if(location != null && area > 0) {
			if(!BukkitUtil.isInRange(entry.getLocation(), location, area))
				return false;
		}
		
		if(location != null && useLocation) {
			if(location.getBlockX() != entry.getX() || location.getBlockY() != entry.getY() || location.getBlockZ() != entry.getZ());
				return false;
		}
		
		if(rollback != entry.getRollback())
			return false;
		
		if(player != null) {
			if(!player.equalsIgnoreCase(entry.getPlayer()))
				return false;
		}
		
		if(entity != null) {
			if(!entity.equalsIgnoreCase(entry.getEntity()))
				return false;
		}
		
		if(since > 0) {
			if(entry.getDate() >= since)
				return false;
		}
		
		if(until > 0) {
			if(entry.getDate() <= until)
				return false;
		}
		return true;
	}
}
