package me.arno.blocklog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.arno.blocklog.logs.BlockEdit;
import me.arno.blocklog.util.Query;

public class Rollback {
	private final Player sender;
	private final String player;
	private final int since;
	private final int until;
	private final int area;
	private final int rollback;
	private final int delay;
	private final int limit;
	
	private String entity;
	
	public Rollback(Player sender, String player, String entity, int since, int until, int area, int delay, int limit, int rollback) {
		this.sender = sender;
		this.player = player;
		this.entity = entity;
		this.since = since;
		this.until = until;
		this.area = area;
		this.rollback = rollback;
		this.delay = delay;
		this.limit = limit;
	}
	
	public int getId() {
		return rollback;
	}
	
	public Player getSender() {
		return sender;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public ArrayList<BlockEdit> getBlocks() {
		World world = sender.getWorld();
		
		Location location = sender.getLocation();
		int xMin = location.getBlockX() - area;
		int xMax = location.getBlockX() + area;
		int yMin = location.getBlockY() - area;
		int yMax = location.getBlockY() + area;
		int zMin = location.getBlockZ() - area;
		int zMax = location.getBlockZ() + area;
		
		Query query = new Query("blocklog_blocks");
		query.select("*");
		if(player != null) {
			query.where("triggered", player);
		}
		if(entity != null) {
			if(entity.equalsIgnoreCase("tnt"))
				entity = "primed_tnt";
			query.where("entity", entity);
		}
		if(since != 0)
			query.where("date", since, ">");
		if(until != 0)
			query.where("date", until, "<");
		if(area != 0) {
			query.where("x", xMin, ">=").where("x", xMax, "<=").where("y", yMin, ">=").where("y", yMax, "<=").where("z", zMin, ">=").where("z", zMax, "<=");
		}
		query.where("world", world.getName());
		query.where("rollback_id", 0);
		query.groupBy("x", "y", "z");
		query.orderBy("date", "DESC");
		
		ArrayList<BlockEdit> blockEdits = new ArrayList<BlockEdit>();
		
		try {
			ResultSet rs = query.getResult();
			
			for(BlockEdit edit : blockEdits) {
				if(checkEdit(edit))
					blockEdits.add(edit);
			}
			
			while(rs.next()) {
				int id = rs.getInt("id");
				String entity = rs.getString("entity");
				String triggered = rs.getString("triggered");
				int block_id = rs.getInt("block_id");
				int data = rs.getInt("datavalue");
				int gamemode = rs.getInt("gamemode");
				int type = rs.getInt("type");
				long date = rs.getLong("date");
				
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				Location loc = new Location(Bukkit.getWorld(rs.getString("world")), x, y, z);
				
				BlockEdit blockEdit = new BlockEdit(id, triggered, entity, block_id, data, gamemode, loc, type, date);
				blockEdit.setRollback(rs.getInt("rollback_id"));
				
				blockEdits.add(blockEdit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blockEdits;
	}
	
	public boolean checkEdit(BlockEdit blockEdit) {
		Location location = sender.getLocation();
		
		if(blockEdit.getWorld() != location.getWorld())
			return false;
		
		if(blockEdit.getRollback() == 0)
			return false;
		
		if(player != null) {
			if(!player.equalsIgnoreCase(blockEdit.getPlayerName()))
				return false;
		}
		
		if(entity != null) {
			if(!entity.equalsIgnoreCase(blockEdit.getEntityName()))
				return false;
		}
		
		if(since != 0) {
			if(blockEdit.getDate() < since)
				return false;
		}
		
		if(until != 0) {
			if(blockEdit.getDate() > until)
				return false;
		}
		
		if(area > 0) {
			if(!(blockEdit.getX() >= location.getX() && blockEdit.getX() <= location.getX() && blockEdit.getY() >= location.getY() && blockEdit.getY() <= location.getY() && blockEdit.getZ() <= location.getZ() && blockEdit.getZ() >= location.getZ()))
				return false;
		}
		
		return true;
	} 
}
