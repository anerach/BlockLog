package me.arno.blocklog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.logs.PlayerChat;
import me.arno.blocklog.logs.PlayerCommand;
import me.arno.blocklog.logs.PlayerDeath;
import me.arno.blocklog.logs.PlayerKill;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class PlayerListener extends BlockLogListener {
	public PlayerListener(BlockLog plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
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
			getQueueManager().queueBlockEdit(player, block, LogType.PLACE);
			BlocksLimitReached();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		BlockState block = event.getBlockClicked().getRelative(event.getBlockFace()).getState();
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
		
		if(!event.isCancelled() && !cancel) {
			if(event.getBucket() == Material.WATER_BUCKET)
				block.setType(Material.WATER);
			else if(event.getBucket() == Material.LAVA_BUCKET)
				block.setType(Material.LAVA);
			
			getQueueManager().queueBlockEdit(player, block, LogType.PLACE);
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getPlayer().getWorld(), LogType.PORTAL)) {
			Block block;
			block = event.getClickedBlock().getRelative(BlockFace.UP);
			if(block.getType() != Material.FIRE)
				block = event.getClickedBlock().getRelative(BlockFace.NORTH);
			if(block.getType() != Material.FIRE)
				block = event.getClickedBlock().getRelative(BlockFace.EAST);
			if(block.getType() != Material.FIRE)
				block = event.getClickedBlock().getRelative(BlockFace.SOUTH);
			if(block.getType() != Material.FIRE)
				block = event.getClickedBlock().getRelative(BlockFace.WEST);
			if(block.getType() == Material.FIRE) {
				getQueueManager().queueBlockEdit(event.getPlayer(), block.getState(), LogType.BREAK);
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getPlayer().getWorld(), LogType.COMMAND)) {
			Player player = event.getPlayer();
			String[] args = event.getMessage().replace('/', ' ').trim().split(" ");
			Command cmd = Bukkit.getPluginCommand(args[0]);
			if(cmd != null) {
				PlayerCommand lcmd = new PlayerCommand(player, event.getMessage());
				lcmd.save();
			}
		}
	}	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if(!event.isCancelled() && getSettingsManager().isLoggingEnabled(event.getPlayer().getWorld(), LogType.CHAT)) {
			Player player = event.getPlayer();
			PlayerChat lchat = new PlayerChat(player, event.getMessage());
			lchat.save();
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
