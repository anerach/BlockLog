package me.arno.blocklog.logs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.util.Query;

import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ChestEntry extends BlockEntry {
	private ItemStack[] items;
	
	public ChestEntry(String player, BlockState block, ItemStack[] items) {
		super(player, EntityType.PLAYER, LogType.INVENTORY_INTERACTION, block);
		this.items = items;
	}
	
	public void saveChestItems() {
		Query query;
		try {
			this.save();
			query = new Query("blocklog_blocks").where("date", getDate());
			query.where("x", getX()).where("y", getY()).where("z", getZ());
			query.select("id");
			
			ResultSet rs = query.getResult();
			rs.next();
			setId(rs.getInt("id"));
			
			query = new Query("blocklog_chests");
			HashMap<String, Object> values = new HashMap<String, Object>();
			values.put("chest", getId());
			
			for(ItemStack item : items) {
				values.put("item", item.getType().getId());
				values.put("amount", item.getAmount());
				values.put("data", item.getData().getData());
				
				query.insert(values);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ItemStack[] getItems() {
		return items;
	}
}
