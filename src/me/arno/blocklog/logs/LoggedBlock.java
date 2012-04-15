package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;


public class LoggedBlock {
	private BlockLog plugin;
	private Log logType;
	
	private Player player;
	private BlockState block;
	
	private long date;
	
	private Integer rollback = 0;
	
	public LoggedBlock(BlockLog plugin, BlockState block, Log type) {
		this(plugin, null, block, type);
	}
	
	public LoggedBlock(BlockLog plugin, Player player, BlockState block, Log type) {
		this.plugin = plugin;
		this.player = player;
		this.block = block;
		this.logType = type;
		this.date = (System.currentTimeMillis()/1000);
	}

	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			block.getData();
			stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, datavalue, world, date, x, y, z, type, rollback_id) VALUES ('" + getPlayerName() + "', " + getBlockId() + ", " + getDataValue() + ", '" + getWorld().getName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getTypeId() + ", " + getRollback() + ")");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public int getBlockId() {
		return block.getTypeId();
	}
	
	public byte getDataValue() {
		return block.getData().getData();
	}
	
	public BlockState getBlock() {
		return block;
	}
	
	public World getWorld() {
		return block.getWorld();
	}
	
	public Location getLocation() {
		return block.getLocation();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getPlayerName() {
		return (player == null) ? "Environment" : player.getName();
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
		return block.getLocation().getBlockX();
	}
	
	public int getY()
	{
		return block.getLocation().getBlockY();
	}
	
	public int getZ()
	{
		return block.getLocation().getBlockZ();
	}
}
