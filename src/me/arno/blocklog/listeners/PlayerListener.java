package me.arno.blocklog.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LoggedChat;
import me.arno.blocklog.logs.LoggedDeath;
import me.arno.blocklog.logs.LoggedKill;

public class PlayerListener extends BlockLogListener {
	
	public PlayerListener(BlockLog plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if(!event.isCancelled() && getConfig().getBoolean("logs.chat")) {
			Player player = event.getPlayer();
			LoggedChat lchat = new LoggedChat(plugin, player, event.getMessage());
			lchat.save();
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntityType() == EntityType.PLAYER) {
			if(event.getEntity() instanceof Player && getConfig().getBoolean("logs.death")) {
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
				
				LoggedDeath ldeath = new LoggedDeath(plugin, player, type);
				ldeath.save();
			}
		} else {
			LivingEntity victem = event.getEntity();
			Player killer = event.getEntity().getKiller();
			
			if(killer != null && getConfig().getBoolean("logs.kill")) {
				LoggedKill lkill = new LoggedKill(plugin, victem, killer);
				lkill.save();
			}
		}
	}
}
