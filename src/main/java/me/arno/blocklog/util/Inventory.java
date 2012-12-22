package me.arno.blocklog.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Inventory {
	
	public static ItemStack[] compareInventories(ItemStack[] oldItems, ItemStack[] newItems) {
		final ItemStackComparator comperator = new ItemStackComparator();
		final ArrayList<ItemStack> diffItems = new ArrayList<ItemStack>();
		final int l1 = oldItems.length, l2 = newItems.length;
		int c1 = 0, c2 = 0;
		while (c1 < l1 || c2 < l2) {
			if (c1 >= l1) {
				diffItems.add(newItems[c2]);
				c2++;
				continue;
			}
			if (c2 >= l2) {
				oldItems[c1].setAmount(oldItems[c1].getAmount() * -1);
				diffItems.add(oldItems[c1]);
				c1++;
				continue;
			}
			final int comp = comperator.compare(oldItems[c1], newItems[c2]);
			if (comp < 0) {
				oldItems[c1].setAmount(oldItems[c1].getAmount() * -1);
				diffItems.add(oldItems[c1]);
				c1++;
			} else if (comp > 0) {
				diffItems.add(newItems[c2]);
				c2++;
			} else {
				final int amount = newItems[c2].getAmount() - oldItems[c1].getAmount();
				if (amount != 0) {
					oldItems[c1].setAmount(amount);
					diffItems.add(oldItems[c1]);
				}
				c1++;
				c2++;
			}
		}
		return diffItems.toArray(new ItemStack[diffItems.size()]);
	}
	
	public static ItemStack[] compressInv(ItemStack[] items) {
		ArrayList<ItemStack> compressedInv = new ArrayList<ItemStack>();
		for(ItemStack item: items) {
			if (item != null) {
				int type = item.getTypeId();
				byte data = rawData(item);
				boolean found = false;
				for(ItemStack compressedItem : compressedInv) {
					if(compressedItem.getTypeId() == type && rawData(compressedItem) == data) {
						compressedItem.setAmount(compressedItem.getAmount() + item.getAmount());
						found = true;
						break;
					}
				}
				
				if(!found) {
					MaterialData md = new MaterialData(type, data);
					ItemStack is = new ItemStack(type, item.getAmount(), (short)0);
					is.setData(md);
					
					compressedInv.add(is);
				}
			}
		}
		
		Collections.sort(compressedInv, new ItemStackComparator());
		return compressedInv.toArray(new ItemStack[compressedInv.size()]);
	}
	
	public static byte rawData(ItemStack item) {
		return item.getType() != null ? item.getData() != null ? item.getData().getData() : 0 : 0;
	}
	
	/**
	 * An {@link ItemStack} comparator
	 * 
	 * @author DiddiZ
	 * 
	 */
	public static class ItemStackComparator implements Comparator<ItemStack>
	{
		@Override
		public int compare(ItemStack a, ItemStack b) {
			final int aType = a.getTypeId(), bType = b.getTypeId();
			if (aType < bType)
				return -1;
			if (aType > bType)
				return 1;
			final byte aData = rawData(a), bData = rawData(b);
			if (aData < bData)
				return -1;
			if (aData > bData)
				return 1;
			return 0;
		}
	}
}
