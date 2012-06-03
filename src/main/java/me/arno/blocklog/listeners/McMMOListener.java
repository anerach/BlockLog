package me.arno.blocklog.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import me.arno.blocklog.logs.LogType;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;

public class McMMOListener extends BlockLogListener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onFakeBlockBreak(FakeBlockBreakEvent event) {
		if(!event.isCancelled()) {
			getQueueManager().queueBlockEdit(event.getPlayer().getName(), event.getBlock().getState(), LogType.BLOCK_BREAK);
			BlocksLimitReached();
		}
	} 
}
