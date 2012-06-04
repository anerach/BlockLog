package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.LogType;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
					getQueueManager().queueBlockEdit(block.getState(), entityType, logType);
				else
					getQueueManager().queueBlockEdit(player, block.getState(), entityType, logType);
			}
		}
	}
}
