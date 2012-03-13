package me.arno.blocklog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.ChatColor;
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
	private Player sender;
	
	/* Create new rollback */
	public Rollback(BlockLog plugin, Player player, int type) {
		this.plugin = plugin;
		this.dbSettings = new DatabaseSettings(plugin);
		this.world = player.getWorld();
		this.sender = player;
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, date, type) VALUES ('" + player.getName() + "', '" + world.getName() + "', " + System.currentTimeMillis()/1000 + ", " + type + ")");
			ResultSet rs = stmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
			rs.first();
			this.id = rs.getInt("id");
			this.blockCount = blocks.size();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/* Get Existing Rollback */
	public Rollback(BlockLog plugin, int id) {
		this.id = id;
		this.plugin = plugin;
		this.dbSettings = new DatabaseSettings(plugin);
		this.blocks = getBlocks();
		
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
		
		this.blockCount = blocks.size();
	}
	
	public ArrayList<LoggedBlock> getBlocks() {
		ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
		try {
			Connection conn = dbSettings.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE rollback_id = %s", id));
			while(rs.next()) {
				Player player = plugin.getServer().getPlayer(rs.getString("player"));
				this.world = plugin.getServer().getWorld(rs.getString("world"));
				Location loc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
				LoggedBlock lb = new LoggedBlock(player, rs.getInt("block_id"), loc, rs.getInt("type"));
				blocks.add(lb);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blocks;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean exists() {
		if(blockCount == 0)
			return false;
		
		return true;
	}
	
	public boolean doRollback(int time) throws SQLException {
		return doRollback(null, time);
	}
	
	public boolean doRollback(Player player, int time) throws SQLException {
		int BlockCount = 0;
		int BlockSize = plugin.blocks.size();
		
		/* Internal Stored Blocks */
		while(BlockSize > BlockCount)
		{
			LoggedBlock LBlock = plugin.blocks.get(BlockCount); 
			if(LBlock.getDate() > time) {
				Material m = Material.getMaterial(LBlock.getBlockId());
				if(player != null) {
					if(player.getName().equalsIgnoreCase(LBlock.getPlayer())) {
						if(LBlock.getType() == 0)
							world.getBlockAt(LBlock.getLocation()).setType(m);
						else
							world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);

						LBlock.setRollback(id);
					}
				} else {
					if(LBlock.getType() == 0)
						world.getBlockAt(LBlock.getLocation()).setType(m);
					else
						world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);

					LBlock.setRollback(id);
				}
				BlockCount++;
			}
		}
		
		Connection conn = dbSettings.getConnection();
		
		Statement stmt = conn.createStatement();
		Statement updateStmt = conn.createStatement();
		
		String Query =  String.format("SELECT id,block_id,type,x,y,z,COUNT(id) as count FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' ORDER BY date DESC", time, world.getName());
		
		if(player != null)
			Query = String.format("SELECT id,block_id,type,x,y,z,COUNT(id) as count FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND player = '%s' ORDER BY date DESC", time, world.getName(), player.getName());
		
		ResultSet rs = stmt.executeQuery(Query);
		
		while(rs.next()) {
			Material m = Material.getMaterial(rs.getInt("block_id"));
			int type = rs.getInt("type");
			if(type == 0)
				player.getWorld().getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(m);
			else
				player.getWorld().getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(Material.AIR);
			
			updateStmt.executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", id, rs.getInt("id")));
		}
		conn.close();
		sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + (rs.getInt("count") + BlockCount) + ChatColor.GOLD + " blocks changed!");
		sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/blundo" + ChatColor.GOLD + " to undo this rollback!");
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
