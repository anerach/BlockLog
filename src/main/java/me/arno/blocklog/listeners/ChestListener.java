package me.arno.blocklog.listeners;

import java.util.HashMap;

import me.arno.blocklog.logs.ChestEntry;
import me.arno.blocklog.util.Inventory;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ChestListener extends BlockLogListener {
	private final HashMap<String, ContainerState> containers = new HashMap<String, ContainerState>();
	
	private void checkInventory(Player player) {
		if(!containers.containsKey(player.getName()))
			return;
		
		ContainerState cont = containers.get(player);
		
		BlockState state = cont.loc.getBlock().getState();
		
		if (!(state instanceof InventoryHolder))
			return;
		
		final ItemStack[] before = cont.items;
		
		final ItemStack[] after = Inventory.compressInv(((InventoryHolder)state).getInventory().getContents());
		final ItemStack[] diff = Inventory.compareInventories(before, after);
		
		if(diff.length <= 0)
			return;
		
		getQueueManager().queueData(new ChestEntry(player.getName(), state, diff));
		
		containers.remove(player.getName());
	}
	
	private void checkInventory(Player player, BlockState block) {
		if(containers.containsKey(player.getName()))
			checkInventory(player);
		
		containers.put(player.getName(), new ContainerState(block.getLocation(), Inventory.compressInv(((InventoryHolder)block).getInventory().getContents())));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(PlayerChatEvent event) {
		checkInventory(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		checkInventory(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		checkInventory(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		checkInventory(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR)
			return;
		
		Player player = event.getPlayer();
		BlockState block = event.getClickedBlock().getState();
		
		if(block instanceof Chest) {
			checkInventory(player, block);
		}
	}
	
	private static class ContainerState {
		
		public final ItemStack[] items;
		public final Location loc;
		
		private ContainerState(Location loc, ItemStack[] items) {
			this.items = items;
			this.loc = loc;
		}
	}
}