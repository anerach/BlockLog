package me.arno.blocklog.util;

import org.bukkit.Location;

public class BukkitUtil {
	
	/**
	 * Check if a location is in radius of another one
	 * 
	 * @param loc1 The location you want to check if it's in the radius of the other given location
	 * @param loc2 The center of the area
	 * @param area The radius of the area
	 * @return true when it's in the radius and false if it isn't
	 */
	public static boolean isInRange(Location loc1, Location loc2, int area) {
		if(area == 0)
			return true;
		
		int xMin = loc2.getBlockX() - area;
		int xMax = loc2.getBlockX() + area;
		int yMin = loc2.getBlockY() - area;
		int yMax = loc2.getBlockY() + area;
		int zMin = loc2.getBlockZ() - area;
		int zMax = loc2.getBlockZ() + area;
		
		return loc1.getX() >= xMin && loc1.getX() <= xMax && loc1.getY() >= yMin && loc1.getY() <= yMax && loc1.getZ() >= zMin && loc1.getZ() <= zMax;
	}
}
