package me.arno.blocklog.search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.DataEntry;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.util.Query;

public class DataSearch {
	private Connection conn;
	private String player;
	private String data;
	
	private String world;
	private Location location;
	
	private int type;
	
	private int since = 0;
	private int until = 0;
	
	private int limit = 5;

	public DataSearch() { this.conn = BlockLog.getInstance().conn; }
	public DataSearch(Connection conn) { this.conn = conn; }
	
	public boolean groupByLocation = true;
	
	public void setPlayer(String player) {
		this.player = player;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public void setType(int type) {
		this.type = type;
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
	}
	
	public ArrayList<DataEntry> getResults() {
		ArrayList<DataEntry> dataEntries = new ArrayList<DataEntry>();
		
		Query query = new Query("blocklog_blocks");
		query.select("*");
		if(player != null)
			query.where("player", player);
		if(data != null)
			query.where("data", data);
		if(world != null)
			query.where("world", world);
		if(location != null)
			query.where("x", location.getBlockX()).where("y", location.getBlockY()).where("z", location.getBlockZ());
		if(since != 0)
			query.where("date", since, ">");
		if(until != 0)
			query.where("date", until, "<");
		if(type != 0)
			query.where("type", type);
		
		if(groupByLocation)
			query.groupBy("x", "y", "z");
		
		query.orderBy("date", "DESC");
		
		try {
			ResultSet rs = query.getResult(conn);
			
			for(DataEntry edit : BlockLog.getInstance().getQueueManager().getDataEntries()) {
				if(limit == 0)
					break;
				if(checkEdit(edit)) {
					dataEntries.add(edit);
					limit--;
				}
			}
			
			while(rs.next()) {
				if(limit == 0)
					break;
				
				int id = rs.getInt("id");
				String player = rs.getString("player");
				String data = rs.getString("data");
				int type = rs.getInt("type");
				long date = rs.getLong("date");
				
				Location loc = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));

				LogType logType = LogType.values()[type];
				
				DataEntry dataEntry = new DataEntry(player, logType, loc, data);
				dataEntry.setId(id);
				dataEntry.setDate(date);
				
				dataEntries.add(dataEntry);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dataEntries;
	}
	
	public boolean checkEdit(DataEntry edit) {
		if(!world.equalsIgnoreCase(edit.getWorld()))
			return false;
		
		if(player != null) {
			if(!player.equalsIgnoreCase(edit.getPlayer()))
				return false;
			
		}
		
		if(data != null) {
			if(!data.equalsIgnoreCase(edit.getData()))
				return false;
		}
		
		if(location != null) {
			if(location.getBlockX() != edit.getX())
				return false;
			if(location.getBlockY() != edit.getY())
				return false;
			if(location.getBlockZ() != edit.getZ())
				return false;
		}
		
		if(type > -1) {
			if(edit.getTypeId() != type)
				return false;
		}
		
		if(since > 0) {
			if(edit.getDate() > since)
				return false;
		}
		
		if(until > 0) {
			if(edit.getDate() < until)
				return false;
		}
		return true;
	}
}
