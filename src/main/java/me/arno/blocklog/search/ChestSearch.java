package me.arno.blocklog.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.ChestEntry;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.util.Query;

public class ChestSearch {
	private Connection conn;
	private String player;
	
	private String world;
	private Location location;
	private int area = 0;
	
	private int since = 0;
	private int until = 0;
	
	private int limit = 5;

	public ChestSearch() { this.conn = BlockLog.getInstance().conn; }
	public ChestSearch(Connection conn) { this.conn = conn; }
	
	public void setPlayer(String player) {
		this.player = player;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public void setLocation(Location location) {
		this.location = location;
		setWorld(location.getWorld().getName());
	}
	
	public void setArea(int area) {
		this.area = area;
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
	}
	
	public ArrayList<ChestEntry> getResults() {
		ArrayList<ChestEntry> chestEntries = new ArrayList<ChestEntry>();
		
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
		
		Query query = new Query("blocklog_blocks");
		query.select("*");
		if(player != null)
			query.where("player", player);
		if(since != 0)
			query.where("date", since, ">");
		if(until != 0)
			query.where("date", until, "<");
		if(area != 0 && location != null)
			query.where("x", xMin, ">=").where("x", xMax, "<=").where("y", yMin, ">=").where("y", yMax, "<=").where("z", zMin, ">=").where("z", zMax, "<=");
		if(world != null)
			query.where("world", world.getName());
		
		query.orderBy("date", "DESC");
		
		try {
			ResultSet rs = query.getResult(conn);
			
			for(ChestEntry interaction : BlockLog.getInstance().getQueueManager().getChestEntries()) {
				if(limit == 0)
					break;
				if(checkChestEntry(interaction)) {
					chestEntries.add(interaction);
					limit--;
				}
			}
			
			while(rs.next()) {
				if(limit == 0)
					break;
				
				int id = rs.getInt("id");
				String player = rs.getString("player");
				int item = rs.getInt("item");
				int amount = rs.getInt("amount");
				byte data = rs.getByte("data");
				int type = rs.getInt("type");
				long date = rs.getLong("date");
				
				Location loc = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
				
				ItemStack is = new ItemStack(item, amount, (short)0, data);
				
				ChestEntry chestEntry = new ChestEntry(player, loc, LogType.values()[type], is);
				chestEntry.setId(id);
				chestEntry.setDate(date);
				
				chestEntries.add(chestEntry);
				limit--;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return chestEntries;
	}
	
	public boolean checkChestEntry(ChestEntry chest) {
		if(!world.equalsIgnoreCase(chest.getWorld()))
			return false;
		
		if(player != null) {
			if(!player.equalsIgnoreCase(chest.getPlayer()))
				return false;
		}
		
		if(since > 0) {
			if(chest.getDate() > since)
				return false;
		}
		
		if(until > 0) {
			if(chest.getDate() < until)
				return false;
		}
		
		if(area > 0) {
			if(!(chest.getX() >= location.getX() && chest.getX() <= location.getX() && chest.getY() >= location.getY() && chest.getY() <= location.getY() && chest.getZ() <= location.getZ() && chest.getZ() >= location.getZ()))
				return false;
		}
		
		return true;
	}
}
