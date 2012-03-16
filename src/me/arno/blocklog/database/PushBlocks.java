package me.arno.blocklog.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;

public class PushBlocks {
	BlockLog plugin;
	Logger log;
	
	public PushBlocks(BlockLog plugin) {
		this.plugin = plugin;
		
		this.log = plugin.log;
		startPush();
	}
	
	public void startPush() {
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			final Connection conn = plugin.conn;
		    public void run() {
		    	if(plugin.blocks.size() > 0) {
			    	LoggedBlock block = plugin.blocks.get(0);
			    	try {
						Statement stmt = conn.createStatement();
						
						stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type, rollback_id) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', " + block.getDate() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ", " + block.getRollback() + ")");
			    	} catch (SQLException e) {
			    		e.printStackTrace();
			    	}
			    	plugin.blocks.remove(0);
		    	}
		    }
		}, 100L, plugin.getConfig().getInt("database.delay") * 20L);
	}
}