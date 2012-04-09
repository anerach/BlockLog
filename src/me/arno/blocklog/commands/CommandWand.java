package me.arno.blocklog.commands;

import java.util.HashMap;

import me.arno.blocklog.BlockLog;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandWand implements CommandExecutor {
	BlockLog plugin;
	HashMap<String, ItemStack> playerItemStack = new HashMap<String, ItemStack>();
	HashMap<String, Integer> playerItemSlot = new HashMap<String, Integer>();
	
	public CommandWand(BlockLog plugin) {
		this.plugin = plugin;
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!cmd.getName().equalsIgnoreCase("blwand"))
			return true;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		Material wand = Material.getMaterial(plugin.getConfig().getInt("blocklog.wand"));
		
		if(player.getInventory().contains(wand) && !playerItemStack.containsKey(player.getName())) {
			if(plugin.users.isEmpty()) {
				plugin.users.add(player.getName());
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
			} else if(plugin.users.contains(player.getName())) {
				plugin.users.remove(player.getName());
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand disabled!");
			} else {
				plugin.users.add(player.getName());
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
			}
		} else {
			if(plugin.users.isEmpty()) {
				playerItemStack.put(player.getName(), player.getItemInHand());
				playerItemSlot.put(player.getName(), player.getInventory().getHeldItemSlot());
				
				player.setItemInHand(new ItemStack(wand, 1));
				
				plugin.users.add(player.getName());
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
			} else if(plugin.users.contains(player.getName())) {
				ItemStack itemStack = playerItemStack.get(player.getName());
				Material itemInHand = player.getItemInHand().getType();
				int invSlot = playerItemSlot.get(player.getName());
				int itemInHandSlot = player.getInventory().getHeldItemSlot();
				
				if(itemInHandSlot == invSlot || (itemInHand == wand && itemInHandSlot != invSlot))
					player.setItemInHand(itemStack);
				else
					player.getInventory().setItem(invSlot, itemStack);
				
				plugin.users.remove(player.getName());
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand disabled!");
			} else {
				playerItemStack.put(player.getName(), player.getItemInHand());
				playerItemSlot.put(player.getName(), player.getInventory().getHeldItemSlot());
				
				player.setItemInHand(new ItemStack(wand, 1));
				
				plugin.users.add(player.getName());
				player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Wand enabled!");
			}
		}
		return true;
	}

}
