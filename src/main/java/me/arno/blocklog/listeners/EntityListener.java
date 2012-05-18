package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.logs.PlayerDeath;
import me.arno.blocklog.logs.PlayerKill;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityListener extends BlockLogListener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getLocation().getWorld(), LogType.EXPLOSION)) {
			Player target = null;
			LogType logType = LogType.EXPLOSION;
			EntityType entityType = EntityType.PLAYER;
			
			if(event.getEntityType() != null) {
				entityType = event.getEntityType();
				if(event.getEntityType() == EntityType.CREEPER) {
					logType = LogType.CREEPER;
					Creeper creeper = (Creeper) event.getEntity();
					if(creeper.getTarget() instanceof Player) {
						target = (Player) creeper.getTarget();
					}
				} else if(event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.FIREBALL) {
					logType = LogType.FIREBALL;
				} else if(event.getEntityType() == EntityType.PRIMED_TNT) {
					logType = LogType.TNT;
				}
			}
			
			for(Block block : event.blockList()) {
				if(block.getType() != Material.TNT) {
					if(target == null)
						getQueueManager().queueBlockEdit(block.getState(), entityType, logType);
					else
						getQueueManager().queueBlockEdit(target, block.getState(), entityType, logType);
				}
			}
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCreatePortal(EntityCreatePortalEvent event) {
		if(!event.isCancelled()) {
			if(getSettingsManager().isLoggingEnabled(event.getEntity().getWorld(), LogType.PORTAL)) {
				if(event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					for(BlockState block : event.getBlocks()) {
						getQueueManager().queueBlockEdit(player, block, LogType.PORTAL);
						BlocksLimitReached();
					}
				} else if(event.getEntity() instanceof EnderDragon) {
					for(BlockState block : event.getBlocks()) {
						getQueueManager().queueBlockEdit(block, EntityType.ENDER_DRAGON, LogType.PORTAL);
					}
				}
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntityType() == EntityType.PLAYER && getSettingsManager().isLoggingEnabled(event.getEntity().getWorld(), LogType.DEATH)) {
			if(event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				
				DamageCause deathCause = event.getEntity().getLastDamageCause().getCause();
				Integer type = 0;
				if(deathCause == DamageCause.ENTITY_ATTACK)
					type = 1;
				else if(deathCause == DamageCause.ENTITY_EXPLOSION)
					type = 2;
				else if(deathCause == DamageCause.DROWNING)
					type = 3;
				else if(deathCause == DamageCause.SUFFOCATION)
					type = 4;
				else if(deathCause == DamageCause.SUICIDE)
					type = 5;
				else if(deathCause == DamageCause.FALL)
					type = 6;
				else if(deathCause == DamageCause.VOID)
					type = 7;
				else if(deathCause == DamageCause.LAVA)
					type = 8;
				else if(deathCause == DamageCause.FIRE)
					type = 9;
				else if(deathCause == DamageCause.CONTACT)
					type = 10;
				else if(deathCause == DamageCause.LIGHTNING)
					type = 11;
				
				PlayerDeath ldeath = new PlayerDeath(player, type);
				ldeath.save();
			}
		} else {
			LivingEntity victem = event.getEntity();
			Player killer = event.getEntity().getKiller();
			
			if(killer != null  && getSettingsManager().isLoggingEnabled(victem.getWorld(), LogType.KILL)) {
				PlayerKill lkill = new PlayerKill(victem, killer);
				lkill.save();
			}
		}
	}
}
