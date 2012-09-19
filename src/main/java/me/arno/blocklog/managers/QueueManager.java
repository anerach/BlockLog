package me.arno.blocklog.managers;

import java.sql.Connection;
import java.util.ArrayList;

import me.arno.blocklog.logs.BlockEntry;
import me.arno.blocklog.logs.ChestEntry;
import me.arno.blocklog.logs.DataEntry;
import me.arno.blocklog.logs.InteractionEntry;

public class QueueManager extends BlockLogManager {
	private Connection conn;
	private final ArrayList<DataEntry> dataEntries = new ArrayList<DataEntry>();
	private final ArrayList<BlockEntry> blockEntries = new ArrayList<BlockEntry>();
	private final ArrayList<InteractionEntry> interactionEntries = new ArrayList<InteractionEntry>();
	private final ArrayList<ChestEntry> chestEntries = new ArrayList<ChestEntry>();
	
	public QueueManager() {
		conn = getDatabaseManager().getConnection();
	}
	
	public void queueData(DataEntry dataEntry) {
		dataEntries.add(dataEntry);
	}
	
	/**
	 * Logs a block edit by a player or the environment
	 * 
	 * @param chestEntry {@link BlockEntry} of the edit that was made
	 */
	public void queueBlock(BlockEntry blockEntry) {
		blockEntries.add(blockEntry);
	}
	
	public void queueInteraction(InteractionEntry interactionEntry) {
		interactionEntries.add(interactionEntry);
	}
	
	/**
	 * Logs a chest interaction by a player
	 * 
	 * @param chestEntry {@link ChestEntry} of the chest that got opened
	 */
	public void queueChest(ChestEntry chestEntry) {
		chestEntries.add(chestEntry);
	}
	
	public ArrayList<BlockEntry> getBlockEntries() {
		return blockEntries;
	}

	public ArrayList<ChestEntry> getChestEntries() {
		return chestEntries;
	}
	
	public ArrayList<InteractionEntry> getInteractionEntries() {
		return interactionEntries;
	}
	
	public ArrayList<DataEntry> getDataEntries() {
		return dataEntries;
	}
	
	/**
	 * Returns true if the queue is empty
	 * 
	 * @return true if the queue is empty
	 */
	public boolean isEditQueueEmpty() {
		return blockEntries.isEmpty();
	}
	
	/**
	 * Returns true if the queue is empty
	 * 
	 * @return true if the queue is empty
	 */
	public boolean isInteractionQueueEmpty() {
		return interactionEntries.isEmpty();
	}
	
	/**
	 * Returns true if the queue is empty
	 * 
	 * @return true if the queue is empty
	 */
	public boolean isDataQueueEmpty() {
		return dataEntries.isEmpty();
	}
	
	/**
	 * Returns true if the queue is empty
	 * 
	 * @return true if the queue is empty
	 */
	public boolean isChestQueueEmpty() {
		return chestEntries.isEmpty();
	}
	
	/**
	 * Returns the amount of queued block edits
	 * 
	 * @return the amount of queued block edits
	 */
	public int getEditQueueSize() {
		return blockEntries.size();
	}
	
	/**
	 * Returns the amount of queued block interactions
	 * 
	 * @return the amount of queued block interactions
	 */
	public int getInteractionQueueSize() {
		return interactionEntries.size();
	}
	
	/**
	 * Returns the amount of queued data entry
	 * 
	 * @return the amount of queued data entry
	 */
	public int getDataQueueSize() {
		return dataEntries.size();
	}
	
	/**
	 * Returns the amount of queued chest interactions
	 * 
	 * @return the amount of queued chest interactions
	 */
	public int getChestQueueSize() {
		return chestEntries.size();
	}
	
	/**
	 * Returns a queued block edit
	 * 
	 * @param index The index of the queued block edit
	 * @return {@link BlockEntry} object that represents the queued block edit
	 */
	public BlockEntry getQueuedEdit(int index) throws IndexOutOfBoundsException {
		return blockEntries.get(index);
	}

	/**
	 * Returns a queued interaction
	 * 
	 * @param index The index of the queued interaction
	 * @return {@link InteractionEntry} object that represents the queued interaction
	 */
	public InteractionEntry getQueuedInteraction(int index) throws IndexOutOfBoundsException {
		return interactionEntries.get(index);
	}
	


	/**
	 * Returns a queued data entry
	 * 
	 * @param index The index of the queued data entry
	 * @return {@link InteractionEntry} object that represents the queued data entry
	 */
	public DataEntry getQueuedData(int index) throws IndexOutOfBoundsException {
		return dataEntries.get(index);
	}
	


	/**
	 * Returns a queued chest interaction
	 * 
	 * @param index The index of the queued chest interaction
	 * @return {@link InteractionEntry} object that represents the queued chest interaction
	 */
	public ChestEntry getQueuedChest(int index) throws IndexOutOfBoundsException {
		return chestEntries.get(index);
	}
	
	/**
	 * Saves the oldest queued block edit
	 */
	public void saveQueuedEdit() {
		saveQueuedEdit(0);
	}
	
	/**
	 * Saves the oldest queued interaction
	 */
	public void saveQueuedInteraction() {
		saveQueuedInteraction(0);
	}
	
	/**
	 * Saves the oldest queued data entry
	 */
	public void saveQueuedData() {
		saveQueuedData(0);
	}
	
	/**
	 * Saves the oldest queued chest interaction
	 */
	public void saveQueuedChest() {
		saveQueuedChest(0);
	}
	
	/**
	 * Saves a queued block edit
	 * 
	 * @param index The index of the queued block interaction
	 */
	public void saveQueuedEdit(int index) {
		try {
			if(getEditQueueSize() > index) {
				BlockEntry edit = getQueuedEdit(index);
				if(edit != null) {
					edit.save(conn);
					blockEntries.remove(index);
				}
			}
		} catch(IndexOutOfBoundsException e) {}
	}
	
	/**
	 * Saves a queued block edit
	 * 
	 * @param index The index of the queued block interaction
	 */
	public void saveQueuedInteraction(int index) {
		try {
			if(getInteractionQueueSize() > index) {
				InteractionEntry interaction = getQueuedInteraction(index);
				if(interaction != null) {
					interaction.save(conn);
					interactionEntries.remove(index);
				}
			}
		} catch(IndexOutOfBoundsException e) {}
	}
	
	/**
	 * Saves a queued data entry
	 * 
	 * @param index The index of the queued data entry
	 */
	public void saveQueuedData(int index) {
		try {
			if(getDataQueueSize() > index) {
				DataEntry data = getQueuedData(index);
				if(data != null) {
					data.save(conn);
					dataEntries.remove(index);
				}
			}
		} catch(IndexOutOfBoundsException e) {}
	}
	
	/**
	 * Saves a queued chest interaction
	 * 
	 * @param index The index of the queued chest interaction
	 */
	public void saveQueuedChest(int index) {
		try {
			if(getChestQueueSize() > index) {
				ChestEntry chest = getQueuedChest(index);
				if(chest != null) {
					chest.save(conn);
					chestEntries.remove(index);
				}
			}
		} catch(IndexOutOfBoundsException e) {}
	}
	
	public boolean isQueueEmpty() {
		return isEditQueueEmpty() && isInteractionQueueEmpty() && isDataQueueEmpty() && isChestQueueEmpty();
	}
	
	public void saveQueue() {
		saveQueuedEdit();
		saveQueuedInteraction();
		saveQueuedData();
		saveQueuedChest();
	}
}
