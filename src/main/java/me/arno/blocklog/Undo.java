package me.arno.blocklog;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.search.BlockSearch;

public class Undo {
	private final Player sender;
	private final int rollback;
	private final int delay;
	private final int limit;
	
	private ArrayList<BlockEntry> blockEntries = new ArrayList<BlockEntry>();
	
	public Undo(Player sender, int delay, int limit, int rollback) {
		this.sender = sender;
		this.rollback = rollback;
		this.delay = delay;
		this.limit = limit;
		
		BlockSearch search = new BlockSearch();
		search.setRollback(rollback);
		
		blockEntries = search.getResults();
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
	
	public ArrayList<BlockEntry> getBlocks() {
		return blockEntries;
	}
}
