package me.arno.blocklog.managers;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.plugin.Plugin;

public class DependencyManager extends BlockLogManager {
	public static final String[] optionalDependencies = {"GriefPrevention", "WorldGuard", "mcMMO", "Pail"};
	private final HashMap<String, Plugin> availableDependencies = new HashMap<String, Plugin>();
	
	public boolean isEnabled(String plugin) {
		return availableDependencies.containsKey(plugin);
	}
	
	public Plugin getDependencie(String plugin) {
		return availableDependencies.get(plugin);
	}
	
	public Set<String> getAvailableDependencies() {
		return availableDependencies.keySet();
	}
	
	public static boolean isOptionalDependencie(String plugin) {
		for(String str : optionalDependencies) {
			if(str.equalsIgnoreCase(plugin)) {
				return true;
			}
		}
		return false;
	}
}
