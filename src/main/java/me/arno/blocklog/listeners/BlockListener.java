package me.arno.blocklog.listeners;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LogType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BlockListener extends BlockLogListener {
	public BlockListener(BlockLog plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.FIRE)) {
			getLogManager().queueBlockEdit(event.getBlock().getState(), LogType.FIRE);
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(!event.isCancelled() && event.getPlayer() != null) {
			if(event.getBlock().getType() == Material.TNT && getSettingsManager().isLoggingEnabled(event.getPlayer().getWorld(), LogType.BREAK)) {
				getLogManager().queueBlockEdit(event.getPlayer(), event.getBlock().getState(), LogType.BREAK);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getLocation().getWorld(), LogType.EXPLOSION)) {
			LogType logType = LogType.EXPLOSION;
			Player target = null;
			if(event.getEntityType() != null) { // Returns null when using a bed in the nether
				if(event.getEntityType() == EntityType.CREEPER) {
					logType = LogType.EXPLOSION_CREEPER;
					Creeper creeper = (Creeper) event.getEntity();
					if(creeper.getTarget() instanceof Player) {
						target = (Player) creeper.getTarget();
					}
				} else if(event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.FIREBALL) {
					logType = LogType.EXPLOSION_FIREBALL;
				} else if(event.getEntityType() == EntityType.PRIMED_TNT) {
					logType = LogType.EXPLOSION_TNT;
				}
			}
			
			for(Block block : event.blockList()) {
				if(block.getType() != Material.TNT) {
					if(target == null)
						getLogManager().queueBlockEdit(block.getState(), event.getEntityType(), logType);
					else
						getLogManager().queueBlockEdit(target, block.getState(), event.getEntityType(), logType);
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeavesDecay(LeavesDecayEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.LEAVES)) {
				getLogManager().queueBlockEdit(event.getBlock().getState(), LogType.LEAVES);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getWorld(), LogType.GROW)) {
				Player player = event.getPlayer();
				for(BlockState block : event.getBlocks()) {
					getLogManager().queueBlockEdit(player, block, LogType.GROW);
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCreatePortal(EntityCreatePortalEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getEntity().getWorld(), LogType.PORTAL)) {
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					for(BlockState block : event.getBlocks()) {
						getLogManager().queueBlockEdit(player, block, LogType.PORTAL);
						BlocksLimitReached();
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockForm(BlockFormEvent event) {
		if(!event.isCancelled()) {
			if(plugin.getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.FORM)) {
				getLogManager().queueBlockEdit(event.getNewState(), LogType.FORM);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		if(!event.isCancelled()) {
			if(plugin.getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.SPREAD)) {
				getLogManager().queueBlockEdit(event.getNewState(), LogType.SPREAD);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFade(BlockFadeEvent event) {
		if(!event.isCancelled()) {
			if(plugin.getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.FADE)) {
				getLogManager().queueBlockEdit(event.getNewState(), LogType.FADE);
				BlocksLimitReached();
			}
		}
	}
}
