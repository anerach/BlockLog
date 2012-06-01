package me.arno.blocklog.schedules;

import java.util.ArrayList;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Rollback;
import me.arno.blocklog.Undo;
import me.arno.blocklog.logs.BlockEdit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RollbackSchedule implements Runnable {
	private final BlockLog plugin;
	private final Player player;
	private final int rollbackID;
	private final int limit;
	private final int totalBlocks;
	
	private ArrayList<BlockEdit> blockEdits = new ArrayList<BlockEdit>();
	
	private int blockCount = 0;	
	private int sid;
	
	public RollbackSchedule(Undo undo) {
		this.plugin = BlockLog.plugin;
		this.player = undo.getSender();
		this.limit = undo.getLimit();
		this.rollbackID = undo.getRollback();
		
		this.blockEdits = undo.getBlocks();
		this.totalBlocks = blockEdits.size();
	}
	
	public RollbackSchedule(Rollback rb) {
		this.plugin = BlockLog.plugin;
		this.player = rb.getSender();
		this.limit = rb.getLimit();
		this.rollbackID = rb.getId();
		
		this.blockEdits = rb.getBlocks();
		this.totalBlocks = blockEdits.size();
	}
	
	public void setId(Integer sid) {
		this.sid = sid;
	}
	
	public int getBlockCount() {
		return totalBlocks;
	}
	
	@Override
	public void run() {
		for(int i=0;i<limit;i++) {
			if(blockEdits.size() > 0) {
				BlockEdit blockEdit = blockEdits.get(0);
				if(blockEdit.rollback(rollbackID))
					blockCount++;
				
				blockEdits.remove(0);
			} else {
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GREEN + blockCount + ChatColor.GOLD + " blocks of the " + ChatColor.GREEN + totalBlocks + ChatColor.GOLD + " blocks changed!");
				player.sendMessage(ChatColor.DARK_RED + "[BlockLog] " + ChatColor.GOLD + "use the command " + ChatColor.GREEN + "/bl undo " + rollbackID + ChatColor.GOLD + " to undo this rollback!");
				plugin.getSchedules().remove(sid);
				plugin.getServer().getScheduler().cancelTask(sid);
				break;
			}
		}
	}
}
