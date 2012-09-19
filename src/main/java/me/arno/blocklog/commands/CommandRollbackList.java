package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.managers.DatabaseManager;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Syntax;

public class CommandRollbackList extends BlockLogCommand {
	public CommandRollbackList() {
		super("blocklog.rollback");
		setCommandUsage("/bl rollbacklist [id <value>] [player <value>] [since <value>] [until <value>] [area <value>]");
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length > 8)
			return false;
		
		if(args.length % 2 != 0) {
			sender.sendMessage("Invalid amount of args");
			return true;
		}
		
		if(!hasPermission(sender)) {
			sender.sendMessage("You don't have permission");
			return true;
		}
		
		Player player = (Player) sender;
		
		try {
			Syntax syn = new Syntax(args);
			
			int id = syn.getInt("id");
			String target = syn.getString("player");
			int untilTime = syn.getTimeFromNow("until");
			int sinceTime = syn.getTimeFromNow("since");
			
			if(untilTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			Query query = new Query(DatabaseManager.databasePrefix + "rollbacks");
			query.leftJoin(DatabaseManager.databasePrefix + "undos", "id", "rollback");
			
			query.select(DatabaseManager.databasePrefix + "rollbacks.id", DatabaseManager.databasePrefix + "rollbacks.player");
			query.selectDate(DatabaseManager.databasePrefix + "rollbacks.date");
			query.selectAs(DatabaseManager.databasePrefix + "undos.player", "uplayer");

			if(id != 0)
				query.where(DatabaseManager.databasePrefix + "rollbacks.id", id);
			if(target != null)
				query.where(DatabaseManager.databasePrefix + "rollbacks.player", target);
			if(sinceTime != 0)
				query.where(DatabaseManager.databasePrefix + "rollbacks.date", sinceTime, ">");
			if(untilTime != 0)
				query.where(DatabaseManager.databasePrefix + "rollbacks.date", untilTime, "<");
			
			query.orderBy(DatabaseManager.databasePrefix + "rollbacks.date", "DESC");
			query.limit(getSettingsManager().getMaxResults());
			
			ResultSet rollbacks = query.getResult();
			
			player.sendMessage(ChatColor.DARK_RED + "BlockLog Rollbacks (" + plugin.getConfig().getString("blocklog.results") + " Last Rollbacks)");
			while(rollbacks.next()) {
				if(rollbacks.getString("uplayer") != null)
					player.sendMessage(ChatColor.YELLOW + "[#" + rollbacks.getString("id") + "]" + ChatColor.BLUE + "[" + rollbacks.getString("date") + "] " + ChatColor.GOLD + rollbacks.getString("player") + ChatColor.GREEN + " Undone by " + ChatColor.GOLD + rollbacks.getString("uplayer"));
				else
					player.sendMessage(ChatColor.YELLOW + "[#" + rollbacks.getString("id") + "]" + ChatColor.BLUE + "[" + rollbacks.getString("date") + "] " + ChatColor.GOLD + rollbacks.getString("player"));
			}
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
