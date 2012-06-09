package me.arno.blocklog.logs;

import java.sql.SQLException;
import java.util.HashMap;

import me.arno.blocklog.util.Query;

import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ChestEntry extends BlockEntry {
	private ItemStack[] items;
	
	public ChestEntry(String player, BlockState block, ItemStack[] items) {
		super(player, EntityType.PLAYER, LogType.INTERACTION, block);
		this.items = items;
	}
	
	@Override
	public void save() {
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
				values.put("amount", itemStack.getAmount());
				values.put("data", itemStack.getData().getData());
				values.put("type", getTypeId());
				
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
