package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class BlockEdit {
	private final int id;
	private final String player;
	private final String entity;
	private final int block;
	private final byte data;
	private final int gamemode;
	private final Location location;
	private final LogType type;
	private final long date;
	
	private int rollback = 0;
	
	public BlockEdit(Player player, BlockState block, EntityType entity, LogType type) {
		this.id = 0;
		this.player = (player != null) ? player.getName() : null;
		this.entity = ((entity == null) ? EntityType.UNKNOWN.toString() : entity.toString()).toLowerCase();
		this.block = block.getTypeId();
		this.data = block.getData().getData();
		this.gamemode = (player != null) ? player.getGameMode().getValue() : 0;
		this.location = block.getLocation();
		this.type = type;
		this.date = System.currentTimeMillis()/1000;
	}
	
	public BlockEdit(int id, String player, String entity, int block, int data, int gamemode, Location location, int type, long date) {
		this.id = id;
		this.player = player;
		this.entity = entity;
		this.block = block;
		this.data = (byte) data;
		this.gamemode = gamemode;
		this.location = location;
		this.type = LogType.values()[type];
		this.date = System.currentTimeMillis()/1000;
	}

	public void save() {
		try {
			Statement stmt = BlockLog.plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_blocks (entity, triggered, block_id, datavalue, gamemode, world, date, x, y, z, type, rollback_id) VALUES ('" + getEntityName() + "', '" + getPlayerName() + "', " + getBlockId() + ", " + getDataValue() + ", " + getPlayerGameMode() + ", '" + getWorldName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getTypeId() + ", " + getRollback() + ")");
		} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public boolean rollback(int rb) {
		try {
			if(rollback == 0) {
				if(!type.isCreateLog())
					getWorld().getBlockAt(location).setTypeIdAndData(block, data, false);
				else
					getWorld().getBlockAt(location).setType(Material.AIR);
			} else {
				if(!type.isCreateLog())
					getWorld().getBlockAt(location).setType(Material.AIR);
				else
					getWorld().getBlockAt(location).setTypeIdAndData(block, data, false);
			}
			
			if(id == 0) {
				this.setRollback(rb);
			} else {
				Statement stmt = BlockLog.plugin.conn.createStatement();
				stmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", rollback, id));
			}
			return true;
		} catch (SQLException e) {
    		e.printStackTrace();
    	}
		return false;
	}
	
	public String getEntityName() {
		return entity.toString().toLowerCase();
	}
	
	public EntityType getEntity() {
		return EntityType.valueOf(entity.toUpperCase());
	}
	
	public int getPlayerGameMode() {
		return gamemode;
	}
	
	public int getBlockId() {
		return block;
	}
	
	public int getDataValue() {
		return data;
	}
	
	public World getWorld() {
		return location.getWorld();
	}
	
	public String getWorldName() {
		return location.getWorld().getName();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}
	
	public String getPlayerName() {
		return (player == null) ? "environment" : player;
	}
	
	public int getRollback() {
		return rollback;
	}
	
	public void setRollback(int id) {
		this.rollback = id;
	}
	
	public long getDate() {
		return date;
	}
	
	public LogType getType() {
		return type;
	}
	
	public int getTypeId() {
		return getType().getId();
	}
	
	public int getX()
	{
		return location.getBlockX();
	}
	
	public int getY()
	{
		return location.getBlockY();
	}
	
	public int getZ()
	{
		return location.getBlockZ();
	}
}
