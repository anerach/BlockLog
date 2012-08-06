package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.DataEntry;
import me.arno.blocklog.logs.LogType;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityListener extends BlockLogListener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		EntityType entityType = EntityType.UNKNOWN;
		LogType logType = LogType.EXPLOSION_OTHER;
		String player = null;
		
		if(!getSettingsManager().isLoggingEnabled(event.getLocation().getWorld(), LogType.EXPLOSION_OTHER))
			return;
			
		if(entity != null) {
			entityType = event.getEntityType();
			
			if(entityType == EntityType.PRIMED_TNT)
				logType = LogType.EXPLOSION_TNT;
			else if(entityType == EntityType.FIREBALL)
				logType = LogType.EXPLOSION_FIREBALL;
			else if(entityType == EntityType.CREEPER) {
				logType = LogType.EXPLOSION_CREEPER;
				Creeper creeper = (Creeper) entity;
				
				if(((Creeper) entity).getTarget() instanceof Player) {
					player = ((Player) creeper.getTarget()).getName();
				}
			}
		}
		
		for(Block block : event.blockList()) {
			if(block.getType() != Material.TNT) {
				if(player == null)
					getQueueManager().queueBlock(new BlockEntry(entityType, logType, block.getState()));
				else
					getQueueManager().queueBlock(new BlockEntry(player, entityType, logType, block.getState()));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		
		if(!(entity instanceof Enderman))
			return;
		
		BlockState state = event.getBlock().getState();
		
		if(event.getTo() == Material.AIR && getSettingsManager().isLoggingEnabled(entity.getWorld(), LogType.ENDERMAN_PICKUP)) {
			getQueueManager().queueBlock(new BlockEntry(null, entity.getType(), LogType.ENDERMAN_PICKUP, state.getLocation(), state.getTypeId(), state.getRawData()));
		} else if(getSettingsManager().isLoggingEnabled(entity.getWorld(), LogType.ENDERMAN_PLACE)) {
			Enderman enderman = (Enderman) entity;
			
			if(enderman.getCarriedMaterial() != null)
				state.setData(enderman.getCarriedMaterial());
			
			getQueueManager().queueBlock(new BlockEntry("Environment", entity.getType(), LogType.ENDERMAN_PLACE, state.getLocation(), state.getTypeId(), state.getRawData(), event.getTo().getId(), (byte) 0));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent entityDamageByEntity = (EntityDamageByEntityEvent) entity.getLastDamageCause();
				Entity damager = entityDamageByEntity.getDamager();
				
				if(damager instanceof Player) {
					if(getSettingsManager().isLoggingEnabled(entity.getWorld(), LogType.PVP_DEATH))
						getQueueManager().queueData(new DataEntry(player.getName(), LogType.PVP_DEATH, player.getLocation(), ((Player) damager).getName()));
				} else {
					if(getSettingsManager().isLoggingEnabled(entity.getWorld(), LogType.PLAYER_DEATH))
						getQueueManager().queueData(new DataEntry(player.getName(), LogType.PLAYER_DEATH, player.getLocation(), damager.getType().getName()));
				}
			}
		}
	}
}
