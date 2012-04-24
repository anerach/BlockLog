package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class LoggedBlock {
	private BlockLog plugin;
	private Log type;
	
	private Player player;
	private BlockState block;
	private EntityType entity;
	
	private long date;
	
	private Integer rollback = 0;
	
	public LoggedBlock(BlockLog plugin, BlockState block, Log type) {
		this(plugin, null, block, EntityType.PLAYER, type);
	}
	
	public LoggedBlock(BlockLog plugin, BlockState block, EntityType entity, Log type) {
		this(plugin, null, block, entity, type);
	}
	
	public LoggedBlock(BlockLog plugin, Player player, BlockState block, Log type) {
		this(plugin, player, block, EntityType.PLAYER, type);
	}
	
	public LoggedBlock(BlockLog plugin, Player player, BlockState block, EntityType entity, Log type) {
		this.plugin = plugin;
		this.player = player;
		this.block = block;
		this.entity = entity;
		this.type = type;
		this.date = (System.currentTimeMillis()/1000);
	}

	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_blocks (entity, trigered, block_id, datavalue, world, date, x, y, z, type, rollback_id) VALUES ('" + getEntityName() + "', '" + getPlayerName() + "', " + getBlockId() + ", " + getDataValue() + ", '" + getWorld().getName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getTypeId() + ", " + getRollback() + ")");
		} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public String getEntityName() {
		return entity.name().toLowerCase();
	}
	
	public EntityType getEntity() {
		return entity;
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
		return (player == null) ? "environment" : player.getName().toLowerCase();
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
		return type;
	}
	
	public int getTypeId() {
		return getType().getId();
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
