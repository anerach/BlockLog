package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Config;
import me.arno.blocklog.Interaction;
import me.arno.blocklog.Log;
import me.arno.blocklog.log.BrokenBlock;
import me.arno.blocklog.log.EnvironmentBlock;
import me.arno.blocklog.log.GrownBlock;
import me.arno.blocklog.log.InteractedBlock;
import me.arno.blocklog.log.PlacedBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class LogListener implements Listener {
	BlockLog plugin;
	Logger log;
	Config cfg;
	float time;
	
	public LogListener(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
		this.cfg = plugin.cfg;
	}
	
	public void sendAdminMessage(String msg) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	if (player.isOp() || player.hasPermission("blocklog.notices")) {
	    		player.sendMessage(msg);
	        }
	    }
	}
	
	public void BlocksLimitReached() {
		int BlockSize = plugin.blocks.size();
		int WarningBlockSize = cfg.getConfig().getInt("blocklog.warning.blocks");
		int WarningDelay = cfg.getConfig().getInt("blocklog.warning.delay") * 1000;
		int WarningRepeat = cfg.getConfig().getInt("blocklog.warning.repeat");
		
		if(BlockSize == plugin.autoSave && BlockSize != 0 && plugin.autoSave != 0) {
			plugin.saveLogs(0);
		} else if(plugin.autoSave == 0 && (BlockSize ==  WarningBlockSize || (BlockSize > WarningBlockSize && (BlockSize % WarningRepeat == 0)))) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() +  WarningDelay;
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + BlockSize + "!");
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "If you want to save all these blocks use " + ChatColor.DARK_BLUE + "/blfullsave" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/blsave <blocks>");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		int BLWand = cfg.getConfig().getInt("blocklog.wand");
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getTypeId() != BLWand || !WandEnabled) {
				PlacedBlock block = new PlacedBlock(plugin, event.getPlayer(), event.getBlockPlaced().getState());
				block.push();
				BlocksLimitReached();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			BrokenBlock block = new BrokenBlock(plugin, event.getPlayer(), event.getBlock().getState());
			block.push();
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		
		if(!event.isCancelled()) {
			Block placedBlock = event.getBlockClicked().getRelative(event.getBlockFace());

			if(event.getBucket() == Material.WATER_BUCKET)
				placedBlock.setType(Material.WATER);
			else if(event.getBucket() == Material.LAVA_BUCKET)
				placedBlock.setType(Material.LAVA);
			
			PlacedBlock block = new PlacedBlock(plugin, event.getPlayer(), placedBlock.getState());
			block.push();
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		if(!event.isCancelled()) {
			EnvironmentBlock block = new EnvironmentBlock(plugin, event.getBlock().getState(), Log.FIRE);
			block.push();
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(!event.isCancelled()) {
			if(event.getBlock().getType() == Material.TNT) {
				BrokenBlock block = new BrokenBlock(plugin, event.getPlayer(), event.getBlock().getState());
				block.push();
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeavesDecay(LeavesDecayEvent event) {
		if(!event.isCancelled()) {
			if(cfg.getConfig().getBoolean("log.leaves")) {
				EnvironmentBlock block = new EnvironmentBlock(plugin, event.getBlock().getState(), Log.LEAVES);
				block.push();
				BlocksLimitReached();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		for(Block block : event.blockList()) {
			EnvironmentBlock explBlock = new EnvironmentBlock(plugin, block.getState(), Log.EXPLOSION);
			explBlock.push();
			BlocksLimitReached();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		if(!event.isCancelled()) {
			if(cfg.getConfig().getBoolean("log.grow")) {
				Player player = event.getPlayer();
				for(BlockState block : event.getBlocks()) {
					GrownBlock envBlock = new GrownBlock(plugin, player, block, Log.GROW);
					envBlock.push();
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCreatePortal(EntityCreatePortalEvent event) {
		if(!event.isCancelled()) {
			if(cfg.getConfig().getBoolean("log.portal")) {
				Player player = (Player) event.getEntity();
				for(BlockState block : event.getBlocks()) {
					PlacedBlock pBlock = new PlacedBlock(plugin, player, block);
					pBlock.push();
					BlocksLimitReached();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockForm(BlockFormEvent event) {
		if(!event.isCancelled()) {
			if(cfg.getConfig().getBoolean("log.form")) {
				EnvironmentBlock block = new EnvironmentBlock(plugin, event.getNewState(), Log.FORM);
				block.push();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		if(!event.isCancelled()) {
			if(cfg.getConfig().getBoolean("log.spread")) {
				EnvironmentBlock block = new EnvironmentBlock(plugin, event.getNewState(), Log.SPREAD);
				block.push();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.isCancelled()) {
			int BLWand = cfg.getConfig().getInt("blocklog.wand");
			boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
			
			if(event.getPlayer().getItemInHand().getTypeId() != BLWand || !WandEnabled) {
				if(event.getClickedBlock().getType().isBlock()) {
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
						BrokenBlock bBlock = new BrokenBlock(plugin, event.getPlayer(), block.getState());
						bBlock.push();
						BlocksLimitReached();
					}
				} else if(event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.DOOR);
					block.push();
					BlocksLimitReached();
				} else if(event.getClickedBlock().getType() == Material.TRAP_DOOR) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.TRAP_DOOR);
					block.push();
					BlocksLimitReached();
				} else if(event.getClickedBlock().getType() == Material.CHEST) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.CHEST);
					block.push();
					BlocksLimitReached();
				} else if(event.getClickedBlock().getType() == Material.DISPENSER) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.DISPENSER);
					block.push();
					BlocksLimitReached();
				} else if(event.getClickedBlock().getType() == Material.STONE_BUTTON) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.BUTTON);
					block.push();
					BlocksLimitReached();
				} else if(event.getClickedBlock().getType() == Material.LEVER) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.LEVER);
					block.push();
					BlocksLimitReached();
				}
			}
		}
	}
}
