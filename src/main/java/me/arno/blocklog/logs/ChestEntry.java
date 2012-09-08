package me.arno.blocklog.logs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.util.Query;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ChestEntry extends DataEntry {
	private ItemStack[] items = null;
	private ItemStack item = null;
	
	// Used for gathering chestEntries from the database
	public ChestEntry(String player, Location loc, LogType type, ItemStack item) {
		super(player, type, loc, null);
		this.item = item;
	}
	
	// Used for storing chestEntries
	public ChestEntry(String player, Location loc, ItemStack[] items) {
		super(player, LogType.CHEST_PUT, loc, null);
		this.items = items;
	}
	
	@Override
	public void save(Connection conn) {
		try {
			Query query = new Query("blocklog_chests");
			HashMap<String, Object> values = new HashMap<String, Object>();
			
			values.put("player", getPlayer());
			values.put("world", getWorld());
			values.put("x", getX());
			values.put("y", getY());
			values.put("z", getZ());
			values.put("date", getDate());
			
			for(ItemStack itemStack : getItems()) {
				if(itemStack.getAmount() > 0)
					setType(LogType.CHEST_PUT);
				else
					setType(LogType.CHEST_TAKE);
				
				values.put("item", itemStack.getType().getId());
				values.put("amount", Math.abs(itemStack.getAmount()));
				values.put("data", itemStack.getData().getData());
				values.put("type", getTypeId());
				
				query.insert(values, conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public ItemStack[] getItems() {
		return items;
	}
}
