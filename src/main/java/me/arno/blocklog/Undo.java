package me.arno.blocklog;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.arno.blocklog.logs.BlockEdit;
import me.arno.blocklog.util.Query;

public class Undo {
	private final Player sender;
	private final int rollback;
	private final int delay;
	private final int limit;
	
	public Undo(Player sender, int delay, int limit, int rollback) {
		this.sender = sender;
		this.rollback = rollback;
		this.delay = delay;
		this.limit = limit;
	}
	
	public int getRollback() {
		return rollback;
	}
	
	public Player getSender() {
		return sender;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public ArrayList<BlockEdit> getBlocks() {
		ArrayList<BlockEdit> blockEdits = new ArrayList<BlockEdit>();
		
		Query query = new Query("blocklog_blocks");
		query.select("*");
		query.where("rollback_id", rollback);
		
		return blockEdits;
	}
}
