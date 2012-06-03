package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.util.Query;

import org.bukkit.Location;

public class DataEntry {
	private String player;
	private LogType type;
	private String world;
	private int x;
	private int y;
	private int z;
	private long date;
	
	private String data;
	
	public DataEntry(String player, LogType type, Location location, String data) {
		this.player = player;
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.date = System.currentTimeMillis()/1000;
		
		this.data = data;
	}
	
	public boolean rollback() {
		return false;
	}
	
	public boolean playerRollback() {
		return false;
	}
	
	public void save() {
		try {
			Query query = new Query("blocklog_data");
			
			HashMap<String, Object> values = new HashMap<String, Object>();
			
			values.put("player", getPlayer());
			values.put("data", getData());
			values.put("world", getWorld());
			values.put("x", getZ());
			values.put("y", getY());
			values.put("z", getX());
			values.put("type", getTypeId());
			values.put("date", getDate());
			
			query.insert(values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getPlayer() {
		return player;
	}
	
	public String getData() {
		return data;
	}
	
	public String getWorld() {
		return world;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getTypeId() {
		return type.getId();
	}
	
	public LogType getType() {
		return type;
	}
	
	public long getDate() {
		return date;
	}
}
