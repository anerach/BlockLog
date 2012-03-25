package me.arno.blocklog.log;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class LoggedBlock {
	private BlockLog plugin;
	private Log logType;
	
	private int block_id;
	private int datavalue;
	
	private Player player;
	private Block block;
	private Location location;
	private World world;
	
	private long date;
	
	private int rollback = 0;
	
	public LoggedBlock(BlockLog plugin, Player player, int block, int datavalue, Location location, int type) {
		this.plugin = plugin;
		this.player = player;
		this.location = location;
		this.world = location.getWorld();
		this.block_id = block;
		this.datavalue = datavalue;
		this.date = System.currentTimeMillis()/1000;
		this.logType = Log.values()[type];
	}
	
	public LoggedBlock(EnvironmentBlock block) {
		this.plugin = block.plugin;
		this.block = block.getBlock();
		this.block_id = block.getBlock().getTypeId();
		this.datavalue = block.getData();
		this.location = block.getLocation();
		this.world = block.getWorld();
		this.date = block.getDate();
		this.logType = block.getType();
	}
	
	public LoggedBlock(BrokenBlock block) {
		this.plugin = block.plugin;
		this.player = block.getPlayer();
		this.block = block.getBlock();
		this.block_id = block.getId();
		this.datavalue = block.getData();
		this.location = block.getLocation();
		this.world = block.getWorld();
		this.date = block.getDate();
		this.logType = Log.BREAK;
	}
	
	public LoggedBlock(PlacedBlock block) {
		this.plugin = block.plugin;
		this.player = block.getPlayer();
		this.block = block.getBlock();
		this.block_id = block.getId();
		this.datavalue = block.getData();
		this.location = block.getLocation();
		this.world = block.getWorld();
		this.date = block.getDate();
		this.logType = Log.PLACE;
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, datavalue, world, date, x, y, z, type, rollback_id) VALUES ('" + getPlayerName() + "', " + getBlockId() + ", " + getDataValue() + ", '" + getWorld().getName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getTypeId() + ", " + getRollback() + ")");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public int getBlockId() {
		return block_id;
	}
	
	public int getDataValue() {
		return datavalue;
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
		return (player != null) ? player.getName() : "Environment";
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
	
	public Log getType() {
		return logType;
	}
	
	public int getTypeId() {
		return logType.getId();
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
