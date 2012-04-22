package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Interaction;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class LoggedInteraction {
	private final BlockLog plugin;
	
	private final Player player;
	private final Location location;
	private final World world;
	
	private final Interaction type;
	private final long date;
	
	public LoggedInteraction(BlockLog plugin, Player player, Location location, Interaction type) {
		this.plugin = plugin;
		this.player = player;
		this.location = location;
		this.world = location.getWorld();
		this.type = type;
		this.date = System.currentTimeMillis()/1000;
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_interactions (player, world, date, x, y, z, type) VALUES ('" + getPlayerName() + "', '" + getWorld().getName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getTypeId() + ")");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public World getWorld() {
		return world;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getPlayerName() {
		return player.getName();
	}
	
	public long getDate() {
		return date;
	}
	
	public int getTypeId() {
		return type.getTypeId();
	}
	
	public Interaction getType() {
		return type;
	}
	
	public int getX()
	{
		return location.getBlockX();
	}
	
	public int getY()
	{
		return location.getBlockY();
	}
	
	public int getZ()
	{
		return location.getBlockZ();
	}
}
