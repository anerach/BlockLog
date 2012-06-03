package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.LogType;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener extends BlockLogListener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		new BlockEntry(event.getPlayer(), EntityType.PLAYER, LogType.BLOCK_PLACE, event.getBlock().getState());
	}
}