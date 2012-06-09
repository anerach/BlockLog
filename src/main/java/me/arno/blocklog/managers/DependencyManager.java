package me.arno.blocklog.managers;

import java.util.HashMap;
import java.util.Set;

import me.arno.blocklog.BlockLog;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class DependencyManager extends BlockLogManager {
	public static final String[] optionalDependencies = {"mcMMO"};
	private final HashMap<String, Plugin> availableDependencies = new HashMap<String, Plugin>();
	
	public DependencyManager() {
		PluginManager pm = BlockLog.plugin.getServer().getPluginManager();
		for(String dependency : optionalDependencies) {
			if(pm.isPluginEnabled(dependency))
				availableDependencies.put(dependency, pm.getPlugin(dependency));
		}
	}
	
	public boolean isDependencyEnabled(String plugin) {
		return availableDependencies.containsKey(plugin);
	}
	
	public Plugin getDependency(String plugin) {
		return availableDependencies.get(plugin);
	}
	
	public Set<String> getAvailableDependencies() {
		return availableDependencies.keySet();
	}
	
	public static boolean isOptionalDependency(String plugin) {
		for(String str : optionalDependencies) {
			if(str.equalsIgnoreCase(plugin)) {
				return true;
			}
		}
		return false;
	}
}
