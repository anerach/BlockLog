package me.arno.blocklog.logs;

public enum LogType {
	BLOCK_BREAK(0, "BREAK", false),
	BLOCK_PLACE(1, "PLACE", true),
	BLOCK_FORM(2, "FORM", true),
	BLOCK_SPREAD(3, "SPREAD", true),
	BLOCK_FADE(4, "FADE", false),
	BLOCK_BURN(5, "BURN", false),
	EXPLOSION_OTHER(6, "EXPLOSION", false),
	EXPLOSION_CREEPER(7, "CREEPER", false),
	EXPLOSION_FIREBALL(8, "FIREBALL", false),
	EXPLOSION_TNT(9, "TNT", false),
	LEAVES_DECAY(10, "DECAY", false),
	TREE_GROW(11, "GROW", true),
	MUSHROOM_GROW(12, "GROW", true),
	LAVA_FLOW(13, "LAVA", true),
	WATER_FLOW(14, "WATER", true),
	SIGN_PLACE(15, "PLACE", true),
	SIGN_BREAK(16, "BREAK", false),
	CHEST_INTERACTION(17, "INTERACTION"),
	DOOR_INTERACTION(18, "INTERACTION"),
	BUTTON_INTERACTION(19, "INTERACTION"),
	LEVER_INTERACTION(20, "INTERACTION"),
	PLAYER_CHAT(21, "CHAT"),
	PLAYER_COMMAND(22, "COMMAND"),
	PLAYER_DEATH(23, "DEATH"),
	MOB_DEATH(24, "DEATH");
	
	int id;
	String name;
	boolean blockCreate;
	
	LogType(int id, String name) {
		this(id, name, false);
	}
	
	LogType(int id, String name, boolean blockCreate) {
		this.id = id;
		this.blockCreate = blockCreate;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}
	
	/**
	 * This method returns whether the {@link LogType} is used when something gets created or not
	 * 
	 * @return boolean true on create, false on destroy
	 */
	public boolean isCreateLog() {
		return blockCreate;
	}
	
	public LogType getType() {
		return this;
	}
}
