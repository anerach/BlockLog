package me.arno.blocklog.listeners;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.BrokenBlocks;
import me.arno.blocklog.BurntBlocks;
import me.arno.blocklog.InteractedBlock;
import me.arno.blocklog.Interaction;
import me.arno.blocklog.PlacedBlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockBurnEvent;

public class LogListener implements Listener {
	BlockLog plugin;
	
	Logger log;
	float time;
	
	public LogListener(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
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
		int WarningBlockSize = plugin.getConfig().getInt("blocklog.warning.blocks");
		int WarningDelay = plugin.getConfig().getInt("blocklog.warning.delay") * 1000;
		int WarningRepeat = plugin.getConfig().getInt("blocklog.warning.repeat");
		
		if(BlockSize >= plugin.autoSave && BlockSize != 0) {
			if(plugin.autoSaveMsg) {
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog][AutoSave] " + ChatColor.GOLD + "Saving " + plugin.blocks.size() + " blocks!");
				plugin.saveBlocks(0);
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog][AutoSave] " + ChatColor.GOLD + "Succesfully saved all the blocks!");
			} else
				plugin.saveBlocks(0);
		} else if(plugin.autoSave == 0 && (BlockSize ==  WarningBlockSize || (BlockSize > WarningBlockSize && BlockSize % WarningRepeat == 0))) {
			if(time < System.currentTimeMillis()) {
				time = System.currentTimeMillis() +  WarningDelay;
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "BlockLog reached an internal storage of " + BlockSize + "!");
				sendAdminMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "If you want to save all these blocks use " + ChatColor.DARK_BLUE + "/blfullsave" + ChatColor.GOLD + " or " + ChatColor.DARK_BLUE + "/blsave <blocks>");
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		int BLWand = plugin.getConfig().getInt("blocklog.wand");
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getTypeId() != BLWand || !WandEnabled) {
				PlacedBlock block = new PlacedBlock(plugin, event.getPlayer(), event.getBlockPlaced());
				block.push();
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			BrokenBlocks block = new BrokenBlocks(plugin, event.getPlayer(), event.getBlock());
			block.push();
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if(!event.isCancelled()) {
			BurntBlocks block = new BurntBlocks(plugin, event.getBlock());
			block.push();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		int BLWand = plugin.getConfig().getInt("blocklog.wand");
		boolean WandEnabled = plugin.users.contains(event.getPlayer().getName());
		
		if(!event.isCancelled()) {
			if(event.getPlayer().getItemInHand().getTypeId() != BLWand || !WandEnabled) {
				if(event.getMaterial() == Material.WOOD_DOOR) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.DOOR);
					block.push();
				} else if(event.getMaterial() == Material.TRAP_DOOR) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.TRAP_DOOR);
					block.push();
				} else if(event.getMaterial() == Material.CHEST) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.CHEST);
					block.push();
				} else if(event.getMaterial() == Material.DISPENSER) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.DISPENSER);
					block.push();
				} else if(event.getMaterial() == Material.STONE_BUTTON) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.BUTTON);
					block.push();
				} else if(event.getMaterial() == Material.LEVER) {
					InteractedBlock block = new InteractedBlock(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.LEVER);
					block.push();
				}
			}
		}
	}
}
