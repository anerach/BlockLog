package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.util.Query;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;

public class BlockEntry extends DataEntry {
	private int id = 0;
	private String entity;
	private int block;
	private byte datavalue;
	private int rollback = 0;
	
	public BlockEntry(String player, EntityType entity, LogType type, BlockState block) {
		super(player, type, block.getLocation(), null);
		
		this.entity = entity.toString().toLowerCase();
		this.block = block.getType().getId();
		this.datavalue = block.getRawData();
	}
	
	public void rollback() {
		World world = Bukkit.getWorld(getWorld());
		if(rollback == 0) {
			if(!getType().isCreateLog())
				world.getBlockAt(getLocation()).setTypeIdAndData(block, getDataValue(), false);
			else
				world.getBlockAt(getLocation()).setType(Material.AIR);
		} else {
			if(!getType().isCreateLog())
				world.getBlockAt(getLocation()).setType(Material.AIR);
			else
				world.getBlockAt(getLocation()).setTypeIdAndData(block, getDataValue(), false);
		}
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
			values.put("x", getX());
			values.put("y", getY());
			values.put("z", getZ());
			values.put("date", getDate());
			
			query.insert(values);
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
