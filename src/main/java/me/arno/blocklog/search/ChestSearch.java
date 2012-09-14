package me.arno.blocklog.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
	
	private int since = 0;
	private int until = 0;
	
	private int limit = 5;
	
	private boolean ignoreLimit = true;

	public ChestSearch() { this.conn = BlockLog.getInstance().getConnection(); }
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
	
	public void setIgnoreLimit(boolean ignore) {
		this.ignoreLimit = ignore;
	}
	
	public ArrayList<ChestEntry> getResults() {
		ArrayList<ChestEntry> chestEntries = new ArrayList<ChestEntry>();
		Query query = new Query("blocklog_chests");
		query.select("*");
		if(player != null)
			query.where("player", player);
		if(since != 0)
			query.where("date", since, ">");
		if(until != 0)
			query.where("date", until, "<");
		if(world != null)
			query.where("world", world);
		if(location != null)
			query.where("x", location.getBlockX()).where("y", location.getBlockY()).where("z", location.getBlockZ());
		
		query.orderBy("date", "DESC");
		
		try {
			ResultSet rs = query.getResult(conn);
			
			for(ChestEntry interaction : BlockLog.getInstance().getQueueManager().getChestEntries()) {
				if(limit == 0 && !ignoreLimit)
					break;
				if(checkChestEntry(interaction)) {
					chestEntries.add(interaction);
					limit--;
				}
			}
			
			while(rs.next()) {
				if(limit == 0 && !ignoreLimit)
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
	
	public boolean checkChestEntry(ChestEntry entry) {
		if(world != null) {
			if(!world.equalsIgnoreCase(entry.getWorld()))
				return false;
		}
		
		if(location != null) {
			if(location.getBlockX() != entry.getX() || location.getBlockY() != entry.getY() || location.getBlockZ() != entry.getZ());
				return false;
		}
		
		if(player != null) {
			if(!player.equalsIgnoreCase(entry.getPlayer()))
				return false;
		}
		
		if(since > 0) {
			if(entry.getDate() < since)
				return false;
		}
		
		if(until > 0) {
			if(entry.getDate() > until)
				return false;
		}
		
		return true;
	}
}
