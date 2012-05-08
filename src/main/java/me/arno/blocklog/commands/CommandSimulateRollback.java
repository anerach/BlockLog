package me.arno.blocklog.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import me.arno.blocklog.BlockLog;

public class CommandSimulateRollback extends BlockLogCommand {

	public CommandSimulateRollback(BlockLog plugin) {
		super("blocklog.rollback");
		setCommandUsage("/bl simrollback");
	}
	
	@Override
	public boolean execute(Player player, Command cmd, String[] args) {
		
		return true;
	}
}
