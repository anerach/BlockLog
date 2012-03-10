package me.arno.blocklog;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LoggedBlock {
	private Player player;
	private int block_id;
	private World world;
	private int type;
	private long date;
	private Location location;
	private double x;
	private double y;
	private double z;
	
	public LoggedBlock(Player player, Block block, int type) {
		this.player = player;
		this.block_id = block.getTypeId();
		this.world = block.getWorld();
		this.date = System.currentTimeMillis()/1000;
		this.type = type;
		this.location = block.getLocation();
		this.x = block.getLocation().getX();
		this.y = block.getLocation().getY();
		this.z = block.getLocation().getZ();
	}
	
	public String getPlayer()
	{
		return player.getName();
	}
	
	public Integer getBlockId()
	{
		return block_id;
	}
	
	
	public String getWorldName()
	{
		return world.getName();
	}
	public long getDate()
	{
		return date;
	}
	
	public int getType()
	{
		return type;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getZ()
	{
		return z;
	}
}
