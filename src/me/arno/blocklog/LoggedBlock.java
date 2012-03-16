package me.arno.blocklog;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LoggedBlock {
	private Player player;
	private int block_id;
	private int rollback = 0;
	private World world;
	private int type;
	private long date;
	private Location location;
	private double x;
	private double y;
	private double z;
	private Connection conn;
	
	public LoggedBlock(Connection conn, Player player, Block block, int type) {
		this.player = player;
		this.block_id = block.getTypeId();
		this.world = block.getWorld();
		this.date = System.currentTimeMillis()/1000;
		this.type = type;
		this.location = block.getLocation();
		this.x = block.getLocation().getX();
		this.y = block.getLocation().getY();
		this.z = block.getLocation().getZ();
		this.conn = conn;
	}
	
	public LoggedBlock(Connection conn, Player player, int block, Location location, int type) {
		this.player = player;
		this.block_id = block;
		this.world = location.getWorld();
		this.date = System.currentTimeMillis()/1000;
		this.type = type;
		this.location = location;
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.conn = conn;
	}

	public void save(BlockLog plugin) {
	    try {
			Statement stmt = conn.createStatement();
			
			stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type, rollback_id) VALUES ('" + getPlayer() + "', " + getBlockId() + ", '" + getWorldName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getType() + ", " + getRollback() + ")");
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	public String getPlayer()
	{
		return player.getName();
	}
	
	public int getRollback()
	{
		return rollback;
	}
	
	public void setRollback(int id) {
		rollback = id;
	}
	
	public Integer getBlockId()
	{
		return block_id;
	}
	
	
	public String getWorldName()
	{
		return world.getName();
	}
	
	public long getDate()
	{
		return date;
	}
	
	public int getType()
	{
		return type;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getZ()
	{
		return z;
	}
}
