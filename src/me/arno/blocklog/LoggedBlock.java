package me.arno.blocklog;

import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class LoggedBlock {
	private BlockLog plugin;
	
	private int block_id;
	
	private Player player;
	private Block block;
	private Location location;
	private World world;
	
	private int type;
	private long date;
	
	private int rollback = 0;
	
	
	public LoggedBlock(BlockLog plugin, Player player, int block, Location location, int type) {
		this.plugin = plugin;
		this.player = player;
		this.location = location;
		this.world = location.getWorld();
		this.block_id = block;
		this.date = System.currentTimeMillis()/1000;
		this.type = type;
	}
	
	public LoggedBlock(BrokenBlocks block) {
		this.plugin = block.plugin;
		this.player = block.getPlayer();
		this.block = block.getBlock();
		this.location = block.getLocation();
		this.world = block.getWorld();
		this.date = block.getDate();
		this.block_id = block.getId();
		this.type = 0;
	}
	
	public LoggedBlock(PlacedBlock block) {
		this.plugin = block.plugin;
		this.player = block.getPlayer();
		this.block = block.getBlock();
		this.location = block.getLocation();
		this.world = block.getWorld();
		this.date = block.getDate();
		this.block_id = block.getId();
		this.type = 1;
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			
			stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type, rollback_id) VALUES ('" + getPlayerName() + "', " + getBlockId() + ", '" + getWorld().getName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getType() + ", " + getRollback() + ")");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public int getBlockId() {
		return block_id;
	}
	
	public Block getBlock() {
		return block;
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
	
	public int getRollback() {
		return rollback;
	}
	
	public void setRollback(int id) {
		this.rollback = id;
	}
	
	public long getDate() {
		return date;
	}
	
	public int getType() {
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
