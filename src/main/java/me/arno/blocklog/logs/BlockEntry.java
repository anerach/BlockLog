package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.util.Query;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
		this(player, entity, type, block.getLocation(), block.getType().getId(), block.getRawData());
	}
	
	public BlockEntry(String player, EntityType entity, LogType type, Location location, int block, byte data) {
		super(player, type, location, null);
		
		this.entity = entity.toString().toLowerCase();
		this.block = block;
		this.datavalue = data;
	}
	
	public boolean rollback(int rollback) {
		try {
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
			
			if(id == 0) {
				this.setRollback(rollback);
			} else {
				Statement stmt = BlockLog.plugin.conn.createStatement();
				stmt.executeUpdate("UPDATE blocklog_blocks SET rollback = 0 WHERE id = " + id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
