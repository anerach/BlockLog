package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.util.Debug;
import me.arno.blocklog.util.Query;

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
			String target = null;
			Integer id = 0;
			Integer untilTime = 0;
			Integer sinceTime = 0;
			Integer area = 0;
			
			for(int i=0;i<args.length;i+=2) {
				String type = args[i];
				String value = args[i+1];
				if(type.equalsIgnoreCase("player")) {
					target = value;
				} else if(type.equalsIgnoreCase("id")) {
					id = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("area")) {
					area = Integer.valueOf(value);
				} else if(type.equalsIgnoreCase("since")) {
					Character c = value.charAt(value.length() - 1);
					sinceTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				} else if(type.equalsIgnoreCase("until")) {
					Character c = value.charAt(value.length() - 1);
					untilTime = convertToUnixtime(Integer.valueOf(value.replace(c, ' ').trim()), c.toString());
				}
			}
			
			if(untilTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			Query query = new Query("blocklog_rollbacks");
			query.leftJoin("blocklog_undos", "id", "rollback_id");
			
			query.select("blocklog_rollbacks.id","blocklog_rollbacks.player");
			query.selectDateAs("blocklog_rollbacks.date", "date");
			
			query.selectAs("blocklog_undos.player", "uplayer");
			
			if(target != null)
				query.where("blocklog_rollbacks.player", target);
			if(id != 0)
				query.where("blocklog_rollbacks.id", id);
			if(area != 0)
				query.where("blocklog_rollbacks.area", area);
			if(sinceTime != 0)
				query.where("blocklog_rollbacks.date", sinceTime, ">");
			if(untilTime != 0)
				query.where("blocklog_rollbacks.date", untilTime, "<");
			
			query.orderBy("blocklog_rollbacks.date", "DESC");
			query.limit(getSettingsManager().getMaxResults());
			
			Debug.SQL(query.getQuery());
			
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
