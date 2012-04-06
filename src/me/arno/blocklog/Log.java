package me.arno.blocklog;

public enum Log {
	BREAK(0),
	PLACE(1),
	FIRE(2),
	EXPLOSION(3),
	LEAVES(4),
	GROW(5),
	PORTAL(6),
	FORM(7),
	SPREAD(8);
	
	int id;
	Log(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Log getType() {
		return this;
	}
}
