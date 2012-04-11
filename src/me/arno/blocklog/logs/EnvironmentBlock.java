package me.arno.blocklog.logs;


import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class EnvironmentBlock {
	public BlockLog plugin;
	public BlockState block;
	public long date;
	public Log type;
	
	public EnvironmentBlock(BlockLog plugin, BlockState block, Log type) {
		this.plugin = plugin;
		this.block = block;
		this.type = type;
		this.date = System.currentTimeMillis()/1000;
	}
	
	public void push() {
		plugin.blocks.add(new LoggedBlock(this));
	}
	
	public int getId() {
		return block.getTypeId();
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
	
	public long getDate() {
		return date;
	}

	public MaterialData getData() {
		return block.getData();
	}
	
	public Log getType() {
		return type;
	}
	
	public int getTypeId() {
		return type.getId();
	}
}
