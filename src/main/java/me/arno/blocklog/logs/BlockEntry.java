package me.arno.blocklog.logs;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.util.Query;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;

public class BlockEntry extends DataEntry {
	private int id = 0;
	private String entity;
	
	private int block;
	private byte datavalue;
	
	private int originalBlock;
	private byte originalData;
	
	private int rollback = 0;
	
	public BlockEntry(LogType type, BlockState block) {
		this("Environment", EntityType.UNKNOWN, type, block.getLocation(), block.getType().getId(), block.getRawData());
	}
	
	public BlockEntry(EntityType entity, LogType type, BlockState block) {
		this("Environment", entity, type, block.getLocation(), block.getType().getId(), block.getRawData());
	}
	
	public BlockEntry(String player, EntityType entity, LogType type, BlockState block) {
		this(player, entity, type, block.getLocation(), block.getType().getId(), block.getRawData());
	}
	
	public BlockEntry(String player, EntityType entity, LogType type, Location location, int block, byte data) {
		this(player, entity, type, location, block, data, 0, (byte) 0);
	}
	
	public BlockEntry(LogType type, BlockState newState, BlockState oldState) {
		this("Environment", EntityType.UNKNOWN, type, (newState != null ? newState : oldState).getLocation(), newState.getTypeId(), newState.getRawData(), oldState.getTypeId(), oldState.getRawData());
	}
	
	public BlockEntry(EntityType entity, LogType type, BlockState newState, BlockState oldState) {
		this("Environment", entity, type, (newState != null ? newState : oldState).getLocation(), (newState != null) ? newState.getTypeId() : 0, (newState != null) ? newState.getRawData() : 0, oldState.getTypeId(), oldState.getRawData());
	}
	
	public BlockEntry(String player, EntityType entity, LogType type, BlockState newState, BlockState oldState) {
		this(player, entity, type, (newState != null ? newState : oldState).getLocation(), newState.getTypeId(), newState.getRawData(), oldState.getTypeId(), oldState.getRawData());
	}
	
	public BlockEntry(String player, EntityType entity, LogType type, Location location, int block, byte data, int originalBlock, byte originalData) {
		super(player, type, location, null);
		
		this.entity = entity.toString().toLowerCase();
		this.block = block;
		this.datavalue = data;
		
		this.originalBlock = originalBlock;
		this.originalData = originalData;
	}
	
	public boolean rollback(int rollback) {
		try {
			World world = Bukkit.getWorld(getWorld());
			Block block = world.getBlockAt(getLocation());
			
			if(this.rollback == 0) {
				block.setTypeIdAndData(this.getOldBlock(), this.getOldDataValue(), false);
			} else {
				block.setTypeIdAndData(this.getBlock(), this.getDataValue(), false);
			}
			
			if(this.getId() == 0) {
				this.setRollback(rollback);
			} else {
				Statement stmt = BlockLog.getInstance().conn.createStatement();
				stmt.executeUpdate("UPDATE blocklog_blocks SET rollback = " + rollback + " WHERE id = " + id);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void save(Connection conn) {
		if(this.getId() > 0)
			return;
		
		try {
			Query query = new Query("blocklog_blocks");
			HashMap<String, Object> values = new HashMap<String, Object>();
			
			values.put("player", getPlayer());
			values.put("entity", getEntity());
			values.put("block", getBlock());
			values.put("data", getDataValue());
			values.put("original_block", getOldBlock());
			values.put("original_data", getOldDataValue());
			values.put("type", getTypeId());
			values.put("rollback", getRollback());
			values.put("world", getWorld());
			values.put("x", getX());
			values.put("y", getY());
			values.put("z", getZ());
			values.put("date", getDate());
			
			query.insert(values, conn);
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
	
	public String getEntity() {
		return entity;
	}
	
	public int getBlock() {
		return block;
	}
	
	public int getOldBlock() {
		return originalBlock;
	}
	
	public byte getDataValue() {
		return datavalue;
	}
	
	public byte getOldDataValue() {
		return originalData;
	}
	
	public int getRollback() {
		return rollback;
	}
	
	public void setRollback(int id) {
		this.rollback = id;
	}
}
