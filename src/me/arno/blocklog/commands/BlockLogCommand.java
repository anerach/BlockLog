package me.arno.blocklog.commands;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;

import me.arno.blocklog.BlockLog;

public class BlockLogCommand {
	public final BlockLog plugin;
	public final Logger log;
	
	public BlockLogCommand(BlockLog plugin) {
		this.plugin = plugin;
		this.log = plugin.log;
	}
	
	public FileConfiguration getConfig() {
		return plugin.cfg.getConfig();
	}
}
