package me.arno.blocklog;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.search.BlockSearch;

public class Rollback {
	private final Player sender;
	private final int rollback;
	private final int delay;
	private final int limit;
	
	private ArrayList<BlockEntry> blockEntries = new ArrayList<BlockEntry>();
	
	public Rollback(Player sender, String player, String entity, int since, int until, int area, int delay, int limit, int rollback) {
		this.sender = sender;
		this.delay = delay;
		this.limit = limit;
		this.rollback = rollback;
		
		BlockSearch search = new BlockSearch();
		search.setPlayer(player);
		search.setEntity(entity);
		search.setLocation(sender.getLocation());
		search.setDate(since, until);
		search.setArea(area);
		search.setRollback(0);
		
		blockEntries = search.getResults();
	}
	
	public int getAffectedBlockCount() {
		return blockEntries.size();
	}
	
	public int getId() {
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
