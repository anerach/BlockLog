package me.arno.blocklog.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.LoggedBlock;
import me.arno.blocklog.database.DatabaseSettings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSave implements CommandExecutor {
	BlockLog plugin;
	Logger log;
	DatabaseSettings dbSettings;
	
	public CommandSave(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	public void saveBlocks(int blockCount) {
		if(plugin.blocks.size() > 0) {
			int StartSize = plugin.blocks.size();
			if(blockCount == 0)
				StartSize = 0;
			
			while(plugin.blocks.size() > StartSize - blockCount) {
				LoggedBlock block = plugin.blocks.get(0);
		    	try {
			    	Connection conn = dbSettings.getConnection();
					Statement stmt = conn.createStatement();
					stmt.executeUpdate("INSERT INTO blocklog_blocks (player, block_id, world, date, x, y, z, type) VALUES ('" + block.getPlayer() + "', " + block.getBlockId() + ", '" + block.getWorldName() + "', " + block.getDate() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ", " + block.getType() + ")");
			    	conn.close();
		    	} catch (SQLException e) {
		    		log.info("[BlockLog][BlockToDatabase][SQL] Exception!");
					log.info("[BlockLog][BlockToDatabase][SQL] " + e.getMessage());
		    	}
		    	plugin.blocks.remove(0);
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		dbSettings = new DatabaseSettings(plugin);
		Player player = null;
		
		if (sender instanceof Player)
			player = (Player) sender;
		
		if(!(commandLabel.equalsIgnoreCase("blsave") || !commandLabel.equalsIgnoreCase("blfullsave")))
			return false;
		
		if (player == null) {
			sender.sendMessage("This command can only be run by a player");
			return true;
		}
		
		if(commandLabel.equalsIgnoreCase("blsave") && (player.isOp() || player.hasPermission("blocklog.save"))) {
			int blockCount = 25;
			if(args.length == 1)
				blockCount = Integer.parseInt(args[0]);
			
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "saving " + blockCount + " block edits!");
			saveBlocks(blockCount);
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved " + blockCount + " block edits!");
			return true;
		} else if(commandLabel.equalsIgnoreCase("blfullsave") && (player.isOp() || player.hasPermission("blocklog.fullsave"))) {
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Saving all the block edits!");
			saveBlocks(0);
			player.sendMessage(ChatColor.DARK_RED +"[BlockLog] " + ChatColor.GOLD + "Successfully saved all the block edits!");
			return true;
		}
		return false;
	}

}
