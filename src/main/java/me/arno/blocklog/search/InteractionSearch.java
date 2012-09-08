package me.arno.blocklog.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.InteractionEntry;
import me.arno.blocklog.util.Query;

public class InteractionSearch {
	private Connection conn;
	private String player;
	
	private String world;
	private Location location;
	
	private int since = 0;
	private int until = 0;
	
	private int limit = 5;

	private boolean ignoreLimit = true;

	public InteractionSearch() { this.conn = BlockLog.getInstance().conn; }
	public InteractionSearch(Connection conn) { this.conn = conn; }
	
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
	
	public ArrayList<InteractionEntry> getResults() {
		ArrayList<InteractionEntry> interactionEntries = new ArrayList<InteractionEntry>();
		
		Query query = new Query("blocklog_interactions");
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
			
			for(InteractionEntry interaction : BlockLog.getInstance().getQueueManager().getInteractionEntries()) {
				if(limit == 0 && !ignoreLimit)
					break;
				if(checkInteraction(interaction)) {
					interactionEntries.add(interaction);
					limit--;
				}
			}
			
			while(rs.next()) {
				if(limit == 0 && !ignoreLimit)
					break;
				
				int id = rs.getInt("id");
				String player = rs.getString("player");
				int block = rs.getInt("block");
				long date = rs.getLong("date");
				
				Location loc = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
				
				InteractionEntry interactionEntry = new InteractionEntry(player, loc, block);
				interactionEntry.setId(id);
				interactionEntry.setDate(date);
				
				interactionEntries.add(interactionEntry);
				limit--;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return interactionEntries;
	}
	
	public boolean checkInteraction(InteractionEntry entry) {
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
