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
	DatabaseSettings dbSettings;
	
	public PushBlocks(BlockLog plugin) {
		this.plugin = plugin;
		
		this.log = plugin.log;
		startPush();
	}
	
	public void startPush() {
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
				dbSettings = new DatabaseSettings(plugin);
		    	if(plugin.blocks.size() > 0) {
			    	LoggedBlock block = plugin.blocks.get(0);
			    	try {
				    	Connection conn = dbSettings.getConnection();
						Statement stmt = conn.createStatement();
						
						stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type, rollback_id) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', " + block.getDate() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ", " + block.getRollback() + ")");
						conn.close();
			    	} catch (SQLException e) {
			    		log.info("[BlockLog][BlockToDatabase][SQL] Exception!");
						log.info("[BlockLog][BlockToDatabase][SQL] " + e.getMessage());
			    	}
			    	plugin.blocks.remove(0);
		    	}
		    }
		}, 200L, plugin.getConfig().getInt("database.delay") * 20L);
	}
}
