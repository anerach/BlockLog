package me.arno.blocklog;

public enum Interaction {
	DOOR(0),
	TRAP_DOOR(1),
	CHEST(2),
	DISPENSER(3),
	BUTTON(4),
	LEVER(5);
	
	int id;
	Interaction(int id) {
		this.id = id;
	}
	
	public int getTypeId() {
		return this.id;
	}
	
	public Interaction getType() {
		return this;
	}
}
