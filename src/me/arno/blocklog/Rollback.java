package me.arno.blocklog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.arno.blocklog.database.DatabaseSettings;

public class Rollback {
	private BlockLog plugin;
	private int id;
	private ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	private int blockCount = 0;
	private DatabaseSettings dbSettings;
	
	private World world;
	
	public Rollback(BlockLog plugin, Player player, int type) {
		this.plugin = plugin;
		dbSettings = new DatabaseSettings(plugin);
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, date, type) VALUES ('" + player.getName() + "', '" + player.getWorld().getName() + "', " + System.currentTimeMillis()/1000 + ", " + type + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rs.first();
			id = rs.getInt("id");
			blockCount = blocks.size();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getBlocks() {
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE rollback_id = %s", id));
			while(rs.next()) {
				Player player = plugin.getServer().getPlayer(rs.getString("player"));
				world = plugin.getServer().getWorld(rs.getString("world"));
				Location loc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
				LoggedBlock lb = new LoggedBlock(player, rs.getInt("block_id"), loc, rs.getInt("type"));
				blocks.add(lb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Rollback(BlockLog plugin, int id) {
		this.id = id;
		dbSettings = new DatabaseSettings(plugin);
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE rollback_id = %s", id));
			while(rs.next()) {
				Player player = plugin.getServer().getPlayer(rs.getString("player"));
				world = plugin.getServer().getWorld(rs.getString("world"));
				Location loc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
				LoggedBlock lb = new LoggedBlock(player, rs.getInt("block_id"), loc, rs.getInt("type"));
				blocks.add(lb);
			}
			conn.close();
			int BlockCount = 0;
			int BlockSize = plugin.blocks.size();
			
			while(BlockSize > BlockCount)
			{
				LoggedBlock LBlock = plugin.blocks.get(BlockCount); 
				if(LBlock.getRollback() == id) {
					blocks.add(LBlock);
					LBlock.save(plugin);
					plugin.blocks.remove(BlockCount);
				}
				BlockCount++;
			}
			
			blockCount = blocks.size();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			
		}
	}
	
	public int getId() {
		return id;
	}
	
	public boolean exists() {
		if(blockCount == 0)
			return false;
		
		return true;
	}
	
	public boolean undo() {
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			
			int BlockCount = 0;
			int BlockSize = blocks.size();
			
			while(BlockSize > BlockCount)
			{
				LoggedBlock LBlock = blocks.get(BlockCount); 
				Material m = Material.getMaterial(LBlock.getBlockId());
				if(LBlock.getType() == 0)
					world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
				else
					world.getBlockAt(LBlock.getLocation()).setType(m);
				
				stmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = 0 WHERE rollback_id = %s", id));
				BlockCount++;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
