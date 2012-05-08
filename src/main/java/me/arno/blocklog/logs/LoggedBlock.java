package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.sql.Statement;

import me.arno.blocklog.BlockLog;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


public class LoggedBlock {
	private final BlockLog plugin;
	private final LogType type;
	
	private final Player player;
	private final BlockState block;
	private final EntityType entity;
	private final GameMode gamemode;
	
	private long date;
	
	private Integer rollback = 0;
	
	public LoggedBlock(BlockLog plugin, BlockState block, LogType type) {
		this(plugin, null, block, EntityType.PLAYER, type);
	}
	
	public LoggedBlock(BlockLog plugin, BlockState block, EntityType entity, LogType type) {
		this(plugin, null, block, entity, type);
	}
	
	public LoggedBlock(BlockLog plugin, Player player, BlockState block, LogType type) {
		this(plugin, player, block, EntityType.PLAYER, type);
	}
	
	public LoggedBlock(BlockLog plugin, Player player, BlockState block, EntityType entity, LogType type) {
		this.plugin = plugin;
		this.player = player;
		this.block = block;
		this.entity = entity;
		this.type = type;
		this.date = (System.currentTimeMillis()/1000);
		this.gamemode  = (player != null) ? player.getGameMode() : null;
	}

	public void save() {
		try {
			Statement stmt = plugin.conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_blocks (entity, trigered, block_id, datavalue, gamemode, world, date, x, y, z, type, rollback_id) VALUES ('" + getEntityName() + "', '" + getPlayerName() + "', " + getBlockId() + ", " + getDataValue() + ", " + getPlayerGameMode() + ", '" + getWorld().getName() + "', " + getDate() + ", " + getX() + ", " + getY() + ", " + getZ() + ", " + getTypeId() + ", " + getRollback() + ")");
		} catch (SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	public String getEntityName() {
		return entity.name().toLowerCase();
	}
	
	public EntityType getEntity() {
		return entity;
	}
	
	public int getPlayerGameMode() {
		return (gamemode != null) ? gamemode.getValue() : 0;
	}
	
	public int getBlockId() {
		return block.getTypeId();
	}
	
	public byte getDataValue() {
		return block.getData().getData();
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
	
	public Player getPlayer() {
		return player;
	}
	
	public String getPlayerName() {
		return (player == null) ? "environment" : player.getName().toLowerCase();
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
		return block.getLocation().getBlockX();
	}
	
	public int getY()
	{
		return block.getLocation().getBlockY();
	}
	
	public int getZ()
	{
		return block.getLocation().getBlockZ();
	}
}
