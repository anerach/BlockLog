package me.arno.blocklog.log;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Interaction;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class InteractedBlock {
public BlockLog plugin;
	
	private Player player;
	private Location location;

	private Interaction type;
	private long date;
	
	public InteractedBlock(BlockLog plugin, Player player, Location location, Interaction type) {
		this.plugin = plugin;
		this.player = player;
		this.location = location;
		this.type = type;
		this.date = System.currentTimeMillis()/1000;
	}
	
	public void push() {
		//LoggedInteraction(this);
	}
	
	public Interaction getType() {
		return type;
	}
	
	public int getTypeId() {
		return type.getTypeId();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public World getWorld() {
		return location.getWorld();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getPlayerName() {
		return player.getName();
	}
	
	public long getDate() {
		return date;
	}
}
