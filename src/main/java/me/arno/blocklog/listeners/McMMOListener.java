package me.arno.blocklog.listeners;

import org.bukkit.event.EventHandler;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LogType;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;

public class McMMOListener extends BlockLogListener {

	public McMMOListener(BlockLog plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onFakeBlockBreak(FakeBlockBreakEvent event) {
		if(!event.isCancelled()) {
			getQueueManager().queueBlockEdit(event.getPlayer(), event.getBlock().getState(), LogType.BREAK);
			BlocksLimitReached();
		}
	} 
}
