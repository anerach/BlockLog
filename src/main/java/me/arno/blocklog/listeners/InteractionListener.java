package me.arno.blocklog.listeners;

import me.arno.blocklog.logs.InteractionType;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener extends BlockLogListener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.isCancelled() && (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if(event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
				getQueueManager().queueBlockInteraction(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.DOOR);
			} else if(event.getClickedBlock().getType() == Material.TRAP_DOOR) {
				getQueueManager().queueBlockInteraction(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.TRAP_DOOR);
			} else if(event.getClickedBlock().getType() == Material.CHEST) {
				getQueueManager().queueBlockInteraction(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.CHEST);
			} else if(event.getClickedBlock().getType() == Material.DISPENSER) {
				getQueueManager().queueBlockInteraction(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.DISPENSER);
			} else if(event.getClickedBlock().getType() == Material.STONE_BUTTON) {
				getQueueManager().queueBlockInteraction(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.BUTTON);
			} else if(event.getClickedBlock().getType() == Material.LEVER) {
				getQueueManager().queueBlockInteraction(event.getPlayer(), event.getClickedBlock().getLocation(), InteractionType.LEVER);
			}
		}
	}
}
