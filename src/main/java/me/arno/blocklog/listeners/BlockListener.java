package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.LogType;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class BlockListener extends BlockLogListener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		BlockState block = event.getBlock().getState();
		Player player = event.getPlayer();
		
		Boolean cancel = !getSettingsManager().isLoggingEnabled(player.getWorld(), LogType.PLACE);
		
		if(getDependencyManager().isDependencyEnabled("GriefPrevention")) {
			GriefPrevention gp = (GriefPrevention) getDependencyManager().getDependency("GriefPrevention");
			Claim claim = gp.dataStore.getClaimAt(block.getLocation(), false, null);
			
			if(claim != null)
				cancel = claim.allowBuild(player) != null;
		}
		
		if(getDependencyManager().isDependencyEnabled("WorldGuard")) {
			WorldGuardPlugin wg = (WorldGuardPlugin) getDependencyManager().getDependency("WorldGuard");
			cancel = !wg.canBuild(player, block.getLocation());
		}
		
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		
		if(event.getPlayer().getItemInHand().getType() == getSettingsManager().getWand() && WandEnabled)
			cancel = true;
		
		if(!event.isCancelled() && !cancel) {
			getQueueManager().queueBlockEdit(player, block, EntityType.PLAYER, LogType.PLACE);
			BlocksLimitReached();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		BlockState block = event.getBlock().getState();
		Player player = event.getPlayer();
		
		Boolean cancel = !getSettingsManager().isLoggingEnabled(player.getWorld(), LogType.BREAK);
		
		if(getDependencyManager().isDependencyEnabled("GriefPrevention")) {
			GriefPrevention gp = (GriefPrevention) getDependencyManager().getDependency("GriefPrevention");
			Claim claim = gp.dataStore.getClaimAt(block.getLocation(), false, null);
			
			if(claim != null)
				cancel = claim.allowBuild(player) != null;
		}
		
		if(getDependencyManager().isDependencyEnabled("WorldGuard")) {
			WorldGuardPlugin wg = (WorldGuardPlugin) getDependencyManager().getDependency("WorldGuard");
			cancel = !wg.canBuild(player, block.getLocation());
		}
		
		if(!event.isCancelled() && !cancel) {
			getQueueManager().queueBlockEdit(player, block, LogType.BREAK);
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockFromTo(BlockFromToEvent event) {
		if(!event.isCancelled()) {
			if(event.getBlock().getType() == Material.DRAGON_EGG) {
				BlockState blockState = event.getToBlock().getState();
				blockState.setType(Material.DRAGON_EGG);
				
				if(getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.BREAK, LogType.PLACE)) {
					getQueueManager().queueBlockEdit(event.getBlock().getState(), LogType.BREAK);
					getQueueManager().queueBlockEdit(blockState, LogType.PLACE);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.FIRE)) {
			getQueueManager().queueBlockEdit(event.getBlock().getState(), LogType.FIRE);
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(!event.isCancelled() && event.getPlayer() != null) {
			if(event.getBlock().getType() == Material.TNT && getSettingsManager().isLoggingEnabled(event.getPlayer().getWorld(), LogType.BREAK)) {
				getQueueManager().queueBlockEdit(event.getPlayer(), event.getBlock().getState(), LogType.BREAK);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeavesDecay(LeavesDecayEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.LEAVES)) {
				getQueueManager().queueBlockEdit(event.getBlock().getState(), LogType.LEAVES);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockForm(BlockFormEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.FORM)) {
				getQueueManager().queueBlockEdit(event.getNewState(), LogType.FORM);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockSpread(BlockSpreadEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.SPREAD)) {
				getQueueManager().queueBlockEdit(event.getBlock().getState(), LogType.FADE);
				getQueueManager().queueBlockEdit(event.getNewState(), LogType.SPREAD);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockFade(BlockFadeEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.FADE)) {
				getQueueManager().queueBlockEdit(event.getNewState(), LogType.FADE);
				BlocksLimitReached();
			}
		}
	}
}
