package me.arno.blocklog.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.arno.blocklog.Rollback;
import me.arno.blocklog.schedules.RollbackSchedule;
import me.arno.blocklog.util.Query;
import me.arno.blocklog.util.Syntax;

public class CommandRollback extends BlockLogCommand {
	public CommandRollback() {
		super("blocklog.rollback");
		setCommandUsage("/bl rollback [delay <value>] [limit <amount>] [player <value>] [since <value>] [until <value>] [area <value>]");
	}
	
	@Override
	public boolean execute(CommandSender sender, Command cmd, String[] args) {
		if(args.length < 2)
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
			
			String target = syn.getString("player");
			String entity = syn.getString("entity");
			int untilTime = syn.getTimeFromNow("until");
			int sinceTime = syn.getTimeFromNow("since");
			int area = syn.getInt("area");
			int limit = syn.getInt("limit", 200);
			int delay = syn.getTime("delay", "3s");
			
			String arg_until = syn.getString("until");
			String arg_since = syn.getString("since");
			String arg_delay = syn.getString("delay");
			
			if(untilTime != 0 && sinceTime > untilTime) {
				player.sendMessage(ChatColor.WHITE + "Until can't be bigger than since.");
				return true;
			}
			
			Query query = new Query("blocklog_rollbacks");
			
			HashMap<String, Object> values = new HashMap<String, Object>();
			values.put("player", player.getName());
			values.put("world", player.getWorld().getName());
			values.put("arg_player", target);
			values.put("arg_entity", entity);
			values.put("arg_since", arg_since);
			values.put("arg_until", arg_until);
			values.put("arg_area", area);
			values.put("arg_delay", arg_delay);
			values.put("arg_limit", limit);
			values.put("date", System.currentTimeMillis()/1000);
			
			query.insert(values);
			
			query = new Query("blocklog_rollbacks");
			query.select("id").orderBy("id", "DESC");
			
			ResultSet rollback = query.getResult();
			rollback.next();
			
			int rollbackID = rollback.getInt("id");
			
			Rollback rb = new Rollback(player, target, entity, sinceTime, untilTime, area, delay, limit, rollbackID);
			RollbackSchedule rbSchedule = new RollbackSchedule(rb);
			int sid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, rbSchedule, 20L, delay * 20L);
			rbSchedule.setId(sid);
			addSchedule(sid, rollbackID);
			
			int blockCount = rb.getAffectedBlockCount();
			
			player.sendMessage(ChatColor.BLUE + "This rollback will affect " + ChatColor.GOLD + blockCount + " blocks");
			player.sendMessage(ChatColor.BLUE + "At a speed of " + ChatColor.GOLD + (limit/delay) + " blocks/second");
			player.sendMessage(ChatColor.BLUE + "It will take about " + ChatColor.GOLD + Math.round(blockCount/(limit/delay)) + " seconds " + ChatColor.BLUE + "to complete the rollback");
			player.sendMessage(ChatColor.BLUE + "To cancel the rollback say " + ChatColor.GOLD + "/bl cancel " + sid);
			return true;
		} catch(NumberFormatException e) {
			return false;
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
