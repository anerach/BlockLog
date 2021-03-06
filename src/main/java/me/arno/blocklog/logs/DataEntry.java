package me.arno.blocklog.logs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.managers.DatabaseManager;
import me.arno.blocklog.util.Query;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DataEntry {
	private int id = 0;
	private String player;
	private LogType type;
	private String world;
	private int x;
	private int y;
	private int z;
	private long date;
	
	private String data;
	
	/**
	 * Creates a new database entry
	 * 
	 * @param player The player who triggered the event
	 * @param type The event type
	 * @param location The location where the event happened
	 * @param data Custom data, can by anything from a name to a number
	 */
	public DataEntry(String player, LogType type, Location location, String data) {
		this.player = player;
		this.type = type;
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.date = System.currentTimeMillis()/1000;
		
		this.data = data;
	}
	
	public HashMap<String, Object> getValues() {
		if(this.getId() > 0)
			return null;
		
		HashMap<String, Object> values = new HashMap<String, Object>();
			

		values.put("player", getPlayer());
		values.put("data", getData());
		values.put("world", getWorld());
		values.put("x", getX());
		values.put("y", getY());
		values.put("z", getZ());
		values.put("type", getTypeId());
		values.put("date", getDate());
		return values;
	}
	
	public void save(Connection conn) {
		try {
			Query query = new Query(DatabaseManager.databasePrefix + "data");
			query.insert(getValues(), conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public String getData() {
		return data;
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
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

	public void setType(LogType type) {
		this.type = type;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
}
