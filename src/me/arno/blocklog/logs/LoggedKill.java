package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LoggedKill {
	private final BlockLog plugin;
	private final LivingEntity victem;
	private final Player killer;
	private final Long time;
	
	public LoggedKill(BlockLog plugin, LivingEntity victem, Player killer) {
		this.plugin = plugin;
		this.victem = victem;
		this.killer = killer;
		this.time = System.currentTimeMillis()/1000;
	}
	
	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_kills (player, killer, date) VALUES ('" + getVictemName() + "', '" + getKillerName() + "', " + time + ")");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getVictemName() {
		EntityType entity = victem.getType();
		if(entity == EntityType.PLAYER)
			return ((Player) victem).getName();
		else
			return entity.getName();
	}
	
	public LivingEntity getVictem() {
		return victem;
	}
	
	public String getKillerName() {
		return killer.getName();
	}
	
	public Player getKiller() {
		return killer;
	}
}
