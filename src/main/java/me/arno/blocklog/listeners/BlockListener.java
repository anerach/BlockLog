package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.LogType;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockListener extends BlockLogListener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if(getSettingsManager().isLoggingEnabled(player.getWorld(), LogType.BLOCK_PLACE))
			getQueueManager().queueBlockEdit(player.getName(), event.getBlock().getState(), LogType.BLOCK_PLACE);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(getSettingsManager().isLoggingEnabled(player.getWorld(), LogType.BLOCK_BREAK))
			getQueueManager().queueBlockEdit(player.getName(), event.getBlock().getState(), LogType.BLOCK_BREAK);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		if(getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.BLOCK_FORM))
			getQueueManager().queueBlockEdit(event.getNewState(), LogType.BLOCK_FORM);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent event) {
		if(getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.BLOCK_SPREAD))
			getQueueManager().queueBlockEdit(event.getNewState(), LogType.BLOCK_SPREAD);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		if(getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.BLOCK_FADE))
			getQueueManager().queueBlockEdit(event.getNewState(), LogType.BLOCK_FADE);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		if(getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.BLOCK_BURN))
			getQueueManager().queueBlockEdit(event.getBlock().getState(), LogType.BLOCK_BURN);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeavesDecay(LeavesDecayEvent event) {
		if(getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.LEAVES_DECAY))
			getQueueManager().queueBlockEdit(event.getBlock().getState(), LogType.LEAVES_DECAY);
	}
}