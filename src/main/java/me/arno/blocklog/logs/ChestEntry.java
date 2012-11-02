package me.arno.blocklog.logs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.managers.DatabaseManager;
import me.arno.blocklog.util.Query;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ChestEntry extends DataEntry {
	private final ItemStack item;
	
	public ChestEntry(String player, Location loc, LogType type, ItemStack item) {
		super(player, type, loc, null);
		this.item = item;
	}
	
	@Override
	public HashMap<String, Object> getValues() {
		if(this.getId() > 0)
			return null;
		
		HashMap<String, Object> values = new HashMap<String, Object>();

		values.put("player", getPlayer());
		values.put("world", getWorld());
		values.put("item", item.getType().getId());
		values.put("amount", Math.abs(item.getAmount()));
		values.put("data", item.getData().getData());
		values.put("type", getTypeId());
		values.put("x", getX());
		values.put("y", getY());
		values.put("z", getZ());
		values.put("date", getDate());
		return values;
	}
	
	@Override
	public void save(Connection conn) {
		try {
			Query query = new Query(DatabaseManager.databasePrefix + "chests");
			query.insert(getValues(), conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack getItem() {
		return item;
	}
}
