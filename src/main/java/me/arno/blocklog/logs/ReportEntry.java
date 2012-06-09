package me.arno.blocklog.logs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.ReportStatus;
import me.arno.blocklog.util.Query;

import org.bukkit.Location;


public class ReportEntry {
	private int id = 0;
	private String player;
	private String message;
	private ReportStatus status = ReportStatus.NEW;
	private String world;
	private int x;
	private int y;
	private int z;
	private long date;
	
	public ReportEntry(int id) {
		try {
			Query query = new Query("blocklog_reports");
			query.where("id", id);
			ResultSet rs = query.getResult();
			if(rs.next()) {
				this.id = id;
				this.player = rs.getString("player");
				this.message = rs.getString("message");
				this.status = ReportStatus.values()[rs.getInt("status")];
				this.world = rs.getString("world");
				this.x = rs.getInt("x");
				this.y = rs.getInt("y");
				this.z = rs.getInt("z");
				this.date = rs.getLong("date");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ReportEntry(String player, String message, Location location) {
		this.player = player;
		this.message = message;
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.date = System.currentTimeMillis()/1000;
	}
	
	public boolean save() {
		try {
			Query query = new Query("blocklog_reports");
			HashMap<String, Object> values = new HashMap<String, Object>();
			
			values.put("player", getPlayer());
			values.put("message", getMessage());
			values.put("status", getStatus());
			values.put("world", getWorld());
			values.put("x", getX());
			values.put("y", getY());
			values.put("z", getZ());
			values.put("date", getDate());
			
			query.insert(values);
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
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

	public String getMessage() {
		return message;
	}

	public ReportStatus getStatus() {
		return status;
	}
	
	public int getStatusId() {
		return status.getId();
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

	public long getDate() {
		return date;
	}
}
