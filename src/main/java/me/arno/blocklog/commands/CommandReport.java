package me.arno.blocklog.commands;

import me.arno.blocklog.logs.ReportEntry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReport extends BlockLogCommand {
	public CommandReport() {
		super("blocklog.report.write");
		setCommandUsage("/bl report <message>");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length < 1)
			return false;
		
		if(!getSettingsManager().isReportsEnabled()) {
			sender.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "The report system is disabled");
			return true;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		Player player = (Player) sender;
		
		String message = "";
		
		for(int i = 0; i < args.length;i++)
			message += ((i == 0) ? "" : " ") + args[i];
		
		ReportEntry report = new ReportEntry(player.getName(), message, player.getLocation());
		report.save();
		player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "Your report has been created");
		return true;
	}
}
