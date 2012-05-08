package me.arno.blocklog.listeners;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.logs.LoggedBlock;
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
			plugin.addBlock(new LoggedBlock(plugin, event.getBlock().getState(), LogType.FIRE));
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(!event.isCancelled() && event.getPlayer() != null) {
			if(event.getBlock().getType() == Material.TNT && getSettingsManager().isLoggingEnabled(event.getPlayer().getWorld(), LogType.BREAK)) {
				plugin.addBlock(new LoggedBlock(plugin, event.getPlayer(), event.getBlock().getState(), LogType.BREAK));
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getLocation().getWorld(), LogType.EXPLOSION)) {
			LogType log = LogType.EXPLOSION;
			Player target = null;
			if(event.getEntityType() != null) {
				if(event.getEntityType() == EntityType.CREEPER) {
					log = LogType.EXPLOSION_CREEPER;
					Creeper creeper = (Creeper) event.getEntity();
					if(creeper.getTarget() instanceof Player)
						target = (Player) creeper.getTarget();
				} else if(event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.FIREBALL) {
					log = LogType.EXPLOSION_GHAST;
				} else if(event.getEntityType() == EntityType.PRIMED_TNT) {
					log = LogType.EXPLOSION_TNT;
				}
			}
			
			for(Block block : event.blockList()) {
				if(block.getType() != Material.TNT) {
					if(target == null)
						plugin.addBlock(new LoggedBlock(plugin, block.getState(), event.getEntityType(), log));
					else
						plugin.addBlock(new LoggedBlock(plugin, target, block.getState(), event.getEntityType(), log));
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeavesDecay(LeavesDecayEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getBlock().getWorld(), LogType.LEAVES)) {
				plugin.addBlock(new LoggedBlock(plugin, event.getBlock().getState(), LogType.LEAVES));
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
					plugin.addBlock(new LoggedBlock(plugin, player, block, LogType.GROW));
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCreatePortal(EntityCreatePortalEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getEntity().getWorld(), LogType.PORTAL)) {
				Player player = (Player) event.getEntity();
				for(BlockState block : event.getBlocks()) {
					plugin.addBlock(new LoggedBlock(plugin, player, block, LogType.PORTAL));
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockForm(BlockFormEvent event) {
		if(!event.isCancelled()) {
			if(plugin.getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.FORM)) {
				plugin.addBlock(new LoggedBlock(plugin, event.getNewState(), LogType.FORM));
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		if(!event.isCancelled()) {
			if(plugin.getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.SPREAD)) {
				plugin.addBlock(new LoggedBlock(plugin, event.getNewState(), LogType.SPREAD));
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFade(BlockFadeEvent event) {
		if(!event.isCancelled()) {
			if(plugin.getSettingsManager().isLoggingEnabled(event.getNewState().getWorld(), LogType.FADE)) {
				plugin.addBlock(new LoggedBlock(plugin, event.getNewState(), LogType.FADE));
				BlocksLimitReached();
			}
		}
	}
}
