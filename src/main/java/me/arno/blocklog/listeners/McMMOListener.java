package me.arno.blocklog.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.LogType;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;

public class McMMOListener extends BlockLogListener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFakeBlockBreak(FakeBlockBreakEvent event) {
		Player player = event.getPlayer();
		if(!getSettingsManager().isLoggingEnabled(player.getWorld(), LogType.BLOCK_BREAK))
			return;
		
		getQueueManager().queueBlock(new BlockEntry(player.getName(), EntityType.PLAYER, LogType.BLOCK_BREAK, event.getBlock().getState()));
	} 
}
