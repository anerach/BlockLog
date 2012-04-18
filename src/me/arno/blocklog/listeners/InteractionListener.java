package me.arno.blocklog.listeners;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Interaction;
import me.arno.blocklog.logs.InteractedBlock;

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
