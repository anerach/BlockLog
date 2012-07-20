package me.arno.blocklog;

import org.bukkit.inventory.ItemStack;

public class WandSettings {
	private int maxResults;
	private ResultType resultType;
	private PlayerItem previousItem;
	
	public WandSettings(int results, PlayerItem previousItem, ResultType resultType) {
		this.maxResults = results;
		this.previousItem = previousItem;
		this.resultType = resultType;
	}
	
	public int getMaxResults() {
		return maxResults;
	}
	
	public ResultType getResultType() {
		return resultType;
	}
	
	public PlayerItem getPreviousItem() {
		return previousItem;
	}
	
	public static class PlayerItem {
		private ItemStack item;
		private int slot;
		
		public PlayerItem(ItemStack item, int slot) {
			this.item = item;
			this.slot = slot;
		}
		
		public ItemStack getItem() {
			return item;
		}

		public int getSlot() {
			return slot;
		}
	}
	
	public enum ResultType {
		ALL(0),
		BLOCKS(1),
		INTERACTIONS(2),
		CHESTS(3);
		
		private int id;
		ResultType(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
}
