package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PlayerKill {
	private final BlockLog plugin;
	private final LivingEntity victem;
	private final Player killer;
	private final Location location;
	private final Long time;
	
	public PlayerKill(LivingEntity victem, Player killer) {
		this.plugin = BlockLog.plugin;
		this.victem = victem;
		this.killer = killer;
		this.time = System.currentTimeMillis()/1000;
		this.location = victem.getLocation();
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_kills (victem, killer, world, x, y, z, date) VALUES ('" + getVictemName() + "', '" + getKillerName() + "', '" + getWorldName() + "', " + getX() + ", " + getY() + ", " + getZ() + ", " + time + ")");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getVictemName() {
		EntityType entity = victem.getType();
		if(entity == EntityType.PLAYER)
			return ((Player) victem).getName().toLowerCase();
		else
			return entity.getName().toLowerCase();
	}
	
	public LivingEntity getVictem() {
		return victem;
	}
	
	public String getKillerName() {
		return killer.getName().toLowerCase();
	}
	
	public Player getKiller() {
		return killer;
	}
	
	public String getWorldName() {
		return location.getWorld().getName();
	}
	
	public World getWorld() {
		return location.getWorld();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Integer getX() {
		return location.getBlockX();
	}
	
	public Integer getY() {
		return location.getBlockY();
	}
	
	public Integer getZ() {
		return location.getBlockZ();
	}
}
