package me.arno.blocklog.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandSave extends BlockLogCommand {
	public CommandSave() {
		super("blocklog.save", true);
		setCommandUsage("/bl save [amount|all]");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 1)
			return false;
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		if(args.length == 0) {
			plugin.saveLogs(100, sender);
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("all")) {
				plugin.saveLogs(0, sender);
			} else {
				plugin.saveLogs(Integer.valueOf(args[0]), sender);
			}
		}
		return true;
	}

}
