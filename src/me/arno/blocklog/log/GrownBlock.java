package me.arno.blocklog.log;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Log;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class GrownBlock {
	public BlockLog plugin;
	private BlockState block;
	private Player player;
	private long date;
	private Log type;
	
	public GrownBlock(BlockLog plugin, Player player, BlockState block, Log type) {
		this.plugin = plugin;
		this.block = block;
		this.player = player;
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
	
	public Player getPlayer() {
		return player;
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
