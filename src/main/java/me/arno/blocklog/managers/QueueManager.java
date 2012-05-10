package me.arno.blocklog.managers;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.arno.blocklog.logs.InteractionType;
import me.arno.blocklog.logs.LogType;
import me.arno.blocklog.logs.BlockEdit;
import me.arno.blocklog.logs.BlockInteraction;

public class QueueManager extends BlockLogManager {
	private final ArrayList<BlockEdit> blockEdits = new ArrayList<BlockEdit>();
	private final ArrayList<BlockInteraction> blockInteractions = new ArrayList<BlockInteraction>();
	
	/**
	 * Logs a block edit by the environment.
	 * This can be either a block that has been created or a block that has been destroyed
	 * 
	 * @param block {@link BlockState} of the block that got destroyed
	 * @param type {@link LogType} of the log
	 */
	public void queueBlockEdit(BlockState block, LogType type) {
		queueBlockEdit(null, block, EntityType.PLAYER, type);
	}
	
	/**
	 * Logs a block edit by a player.
	 * This can be either a block that has been created or a block that has been destroyed
	 * 
	 * @param player The player that triggered the event
	 * @param block {@link BlockState} of the block that got destroyed
	 * @param type {@link LogType} of the log
	 */
	public void queueBlockEdit(Player player, BlockState block, LogType type) {
		queueBlockEdit(player, block, null, type);
	}
	
	/**
	 * Logs a block edit by an entity other than a player.
	 * This can be either a block that has been created or a block that has been destroyed
	 * 
	 * @param block {@link BlockState} of the block that got destroyed
	 * @param entity {@link EntityType} of the entity that triggered this event
	 * @param type {@link LogType} of the log
	 */
	public void queueBlockEdit(BlockState block, EntityType entity, LogType type) {
		queueBlockEdit(null, block, entity, type);
	}
	
	/**
	 * Logs a block edit by an entity that got triggered by a player.
	 * This can be either a block that has been created or a block that has been destroyed
	 * 
	 * @param player The {@link Player} that triggered the event
	 * @param block {@link BlockState} of the block that got destroyed
	 * @param entity {@link EntityType} of the entity that triggered this event
	 * @param type {@link LogType} of the log
	 */
	public void queueBlockEdit(Player player, BlockState block, EntityType entity, LogType type) {
		blockEdits.add(new BlockEdit(player, block, entity, type));
	}
	
	/**
	 * Logs a block interaction by a player.
	 * This can be either when a player uses a lever or when he opens a chest
	 * 
	 * @param player The {@link Player} that triggered the event
	 * @param location The {@link Location} where this event happened
	 * @param type {@link InteractionType} of the log
	 */
	public void queueBlockInteraction(Player player, Location location, InteractionType type) {
		blockInteractions.add(new BlockInteraction(player, location, type));
	}
	
	/**
	 * Gets a list of unsaved block edits
	 * 
	 * @return An {@link ArrayList} containing all the unsaved block edits
	 */
	public ArrayList<BlockEdit> getEditQueue() {
		return blockEdits;
	}
	
	/**
	 * Gets a list of unsaved block interactions
	 * 
	 * @return An {@link ArrayList} containing all the unsaved block interactions
	 */
	public ArrayList<BlockInteraction> getInteractionQueue() {
		return blockInteractions;
	}
	
	/**
	 * Gets the amount of unsaved block edits
	 * 
	 * @return An integer value that represents the amount of unsaved block edits
	 */
	public int getEditQueueSize() {
		return blockEdits.size();
	}
	
	/**
	 * Gets the amount of unsaved block interactions
	 * 
	 * @return An integer value that represents the amount of unsaved block interactions
	 */
	public int getInteractionQueueSize() {
		return blockInteractions.size();
	}
}
