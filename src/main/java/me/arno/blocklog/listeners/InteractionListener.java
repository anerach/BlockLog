package me.arno.blocklog.listeners;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Interaction;
import me.arno.blocklog.logs.LoggedInteraction;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener extends BlockLogListener {
	public InteractionListener(BlockLog plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.isCancelled()) {
			if(event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
				plugin.addInteraction(new LoggedInteraction(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.DOOR));
			} else if(event.getClickedBlock().getType() == Material.TRAP_DOOR) {
				plugin.addInteraction(new LoggedInteraction(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.TRAP_DOOR));
			} else if(event.getClickedBlock().getType() == Material.CHEST) {
				plugin.addInteraction(new LoggedInteraction(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.CHEST));
			} else if(event.getClickedBlock().getType() == Material.DISPENSER) {
				plugin.addInteraction(new LoggedInteraction(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.DISPENSER));
			} else if(event.getClickedBlock().getType() == Material.STONE_BUTTON) {
				plugin.addInteraction(new LoggedInteraction(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.BUTTON));
			} else if(event.getClickedBlock().getType() == Material.LEVER) {
				plugin.addInteraction(new LoggedInteraction(plugin, event.getPlayer(), event.getClickedBlock().getLocation(), Interaction.LEVER));
			}
		}
	}
}
