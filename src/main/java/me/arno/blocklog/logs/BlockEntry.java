package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.util.Query;

import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;

public class BlockEntry extends DataEntry {
	private String entity;
	private int block;
	private byte datavalue;
	private int rollback = 0;
	
	public BlockEntry(String player, EntityType entity, LogType type, BlockState block) {
		super(player, type, block.getLocation(), null);
		
		this.entity = entity.toString();
		this.block = block.getType().getId();
		this.datavalue = block.getRawData();
	}

	@Override
	public void save() {
		try {
			Query query = new Query("blocklog_blocks");
			HashMap<String, Object> values = new HashMap<String, Object>();
			
			values.put("player", getPlayer());
			values.put("entity", getEntity());
			values.put("block", getBlock());
			values.put("data", getDataValue());
			values.put("type", getTypeId());
			values.put("rollback", getRollback());
			values.put("world", getWorld());
			values.put("x", getZ());
			values.put("y", getY());
			values.put("z", getX());
			values.put("date", getDate());
			
			query.insert(values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getEntity() {
		return entity;
	}
	
	public int getBlock() {
		return block;
	}
	
	public byte getDataValue() {
		return datavalue;
	}
	
	public int getRollback() {
		return rollback;
	}
	
	public void setRollback(int id) {
		this.rollback = id;
	}
}
