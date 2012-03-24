package me.arno.blocklog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import me.arno.blocklog.log.LoggedBlock;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Rollback {
	private BlockLog plugin;
	private int id;
	private ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
	private int blockCount = 0;
	
	private World world;
	private Player sender;
	private Connection conn;
	
	/* Create new rollback */
	public Rollback(BlockLog plugin, Player player, int type) throws SQLException {
		this.plugin = plugin;
		this.world = player.getWorld();
		this.sender = player;
		this.conn = plugin.conn;
		
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO blocklog_rollbacks (player, world, date, type) VALUES ('" + player.getName() + "', '" + world.getName() + "', " + System.currentTimeMillis()/1000 + ", " + type + ")");
		
		ResultSet rs = stmt.executeQuery("SELECT id FROM blocklog_rollbacks ORDER BY id DESC");
		rs.next();
		
		this.id = rs.getInt("id");
		this.blockCount = blocks.size();
	}
	
	/* Get existing Rollback */
	public Rollback(BlockLog plugin, int id) throws SQLException {
		this.id = id;
		this.plugin = plugin;
		this.conn = plugin.conn;
		
		
		this.blocks = getBlocks();
		this.blockCount = blocks.size();
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT player FROM blocklog_rollbacks WHERE id = " + id);
		rs.next();
		this.sender = plugin.getServer().getPlayer(rs.getString("player"));
	}
	
	public ArrayList<LoggedBlock> getBlocks() {
		ArrayList<LoggedBlock> blocks = new ArrayList<LoggedBlock>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM blocklog_blocks WHERE rollback_id = %s", id));
			while(rs.next()) {
				Player player = plugin.getServer().getPlayer(rs.getString("player"));
				this.world = plugin.getServer().getWorld(rs.getString("world"));
				Location loc = new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
				LoggedBlock lb = new LoggedBlock(plugin, player, rs.getInt("block_id"), rs.getInt("datavalue"), loc, rs.getInt("type"));
				blocks.add(lb);
			}
			
			int BlockCount = 0;
			int BlockSize = plugin.blocks.size();
			
			ArrayList<LoggedBlock> LBlocks = plugin.blocks;
			
			while(BlockSize > BlockCount)
			{
				if(plugin.blocks.size() >= BlockCount) {
					LoggedBlock LBlock = LBlocks.get(0);
					
					if(LBlock.getRollback() == id) {
						blocks.add(LBlock);
						LBlocks.remove(0);
					}
					BlockCount++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blocks;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean exists() throws SQLException {
		if(blockCount == 0)
			return false;
		
		if(conn == null)
			return false;
		
		return true;
	}
	
	public boolean doRollback(int time) throws SQLException {
		return doRollback(null, time, 0);
	}
	
	public boolean doRollback(Player player, int time) throws SQLException {
		return doRollback(player, time, 0);
	}
	
	public boolean doRollback(int time, int radius) throws SQLException {
		return doRollback(null, time, radius);
	}
	
	public boolean doRollback(final Player player, final int time, final int radius) throws SQLException {
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
		    public void run() {
		    	try {
			    	int BlockCount = 0;
					int BlockSize = plugin.blocks.size();
					
					int xMin = sender.getLocation().getBlockX() - radius;
					int xMax = sender.getLocation().getBlockX() + radius;
					int yMin = sender.getLocation().getBlockY() - radius;
					int yMax = sender.getLocation().getBlockY() + radius;
					int zMin = sender.getLocation().getBlockZ() - radius;
					int zMax = sender.getLocation().getBlockZ() + radius;
					
					if(sender.getWorld().getMaxHeight() < yMax)
						yMax =	sender.getWorld().getMaxHeight();
					if(0 > yMin)
						yMin = 0;
					
					/* Internal Stored Blocks */
					while(BlockSize > BlockCount)
					{
						LoggedBlock LBlock = plugin.blocks.get(BlockCount); 
						if(LBlock.getDate() > time) {
							Material m = Material.getMaterial(LBlock.getBlockId());
							if(radius == 0) {
								if(player != null) {
									if(player.getName().equalsIgnoreCase(LBlock.getPlayerName())) {
										if(LBlock.getType() == Log.BREAK || LBlock.getType() == Log.FIRE || LBlock.getType() == Log.EXPLOSION)
											world.getBlockAt(LBlock.getLocation()).setTypeIdAndData(m.getId(), (byte) LBlock.getDataValue(), false);
										else
											world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
				
										LBlock.setRollback(id);
									}
								} else {
									if(LBlock.getType() == Log.BREAK || LBlock.getType() == Log.FIRE || LBlock.getType() == Log.EXPLOSION)
										world.getBlockAt(LBlock.getLocation()).setTypeIdAndData(m.getId(), (byte) LBlock.getDataValue(), false);
									else
										world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
				
									LBlock.setRollback(id);
								}
								BlockCount++;
							} else {
								if((LBlock.getX() >= xMin && LBlock.getX() <= xMax ) && (LBlock.getY() >= yMin && LBlock.getY() <= yMax ) && (LBlock.getZ() >= zMin && LBlock.getZ() <= zMax )) {
									if(player != null) {
										if(player.getName().equalsIgnoreCase(LBlock.getPlayerName())) {
											if(LBlock.getType() == Log.BREAK || LBlock.getType() == Log.FIRE || LBlock.getType() == Log.EXPLOSION)
												world.getBlockAt(LBlock.getLocation()).setTypeIdAndData(m.getId(), (byte) LBlock.getDataValue(), false);
											else
												world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
					
											LBlock.setRollback(id);
										}
									} else {
										if(LBlock.getType() == Log.BREAK || LBlock.getType() == Log.FIRE || LBlock.getType() == Log.EXPLOSION)
											world.getBlockAt(LBlock.getLocation()).setTypeIdAndData(m.getId(), (byte) LBlock.getDataValue(), false);
										else
											world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
					
										LBlock.setRollback(id);
									}
									BlockCount++;
								}
							}
						}
						
					}
					
					String Query;
					
					if(radius == 0) {
						Query =  String.format("SELECT * FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' GROUP BY x, y, z ORDER BY date DESC", time, world.getName());
						
						if(player != null)
							Query = String.format("SELECT * FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND player = '%s' GROUP BY x, y, z ORDER BY date DESC", time, world.getName(), player.getName());
					} else {
						Query = String.format("SELECT * FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND x >= %s AND x <= %s AND y >= %s AND y <= %s AND z >= %s AND z <= %s GROUP BY x, y, z ORDER BY date DESC", time, world.getName(), xMin,xMax,yMin,yMax,zMin,zMax);
						
						if(player != null)
							Query = String.format("SELECT * FROM blocklog_blocks WHERE date > '%s' AND rollback_id = 0 AND world = '%s' AND x >= %s AND x <= %s AND y >= %s AND y <= %s AND z >= %s AND z <= %s AND player = '%s' GROUP BY x, y, z ORDER BY date DESC", time, world.getName(), xMin,xMax,yMin,yMax,zMin,zMax, player.getName());
					}
					
					ResultSet rs = conn.createStatement().executeQuery(Query);
					int i = 0;
					while(rs.next()) {
						Material m = Material.getMaterial(rs.getInt("block_id"));
						int datavalue = rs.getInt("datavalue");
						Log type = Log.values()[rs.getInt("type")];
						if(type == Log.BREAK || type == Log.FIRE || type == Log.EXPLOSION)
							world.getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setTypeIdAndData(m.getId(), (byte) datavalue, false);
						else
							world.getBlockAt(rs.getInt("x"),rs.getInt("y"),rs.getInt("z")).setType(Material.AIR);
						
						conn.createStatement().executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = %s WHERE id = %s", id, rs.getInt("id")));
						
						i++;
					}
					sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + (i + BlockCount) + ChatColor.GOLD + " blocks changed!");
					sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/blundo" + ChatColor.GOLD + " to undo this rollback!");
		    	} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		});
		return true;
	}
	
	public boolean undo() {
		plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
		    public void run() {
				try {
					int BlockCount = 0;
					int BlockSize = blocks.size();
					
					while(BlockSize > BlockCount)
					{
						LoggedBlock LBlock = blocks.get(BlockCount); 
						Material m = Material.getMaterial(LBlock.getBlockId());
						if(LBlock.getType() == Log.BREAK || LBlock.getType() == Log.FIRE || LBlock.getType() == Log.EXPLOSION)
							world.getBlockAt(LBlock.getLocation()).setType(Material.AIR);
						else
							world.getBlockAt(LBlock.getLocation()).setTypeIdAndData(m.getId(), (byte) LBlock.getDataValue(), false);
						
						BlockCount++;
					}
					conn.createStatement().executeUpdate(String.format("UPDATE blocklog_blocks SET rollback_id = 0 WHERE rollback_id = %s", id));
					sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "successfully undone rollback #" + id);
					sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + BlockCount + ChatColor.GOLD + " blocks changed!");
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		});
		return true;
	}
}
