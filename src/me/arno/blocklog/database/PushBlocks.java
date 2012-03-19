package me.arno.blocklog.database;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;

public class PushBlocks {
	BlockLog plugin;
	
	public PushBlocks(BlockLog plugin) {
		this.plugin = plugin;
		
		startPush();
	}
	
	public void startPush() {
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			public void run() {
		    	if(plugin.blocks.size() > 0) {
			    	LoggedBlock block = plugin.blocks.get(0);
			    	block.save();
			    	plugin.blocks.remove(0);
		    	}
		    }
		}, 100L, plugin.getConfig().getInt("database.delay") * 20L);
	}
}