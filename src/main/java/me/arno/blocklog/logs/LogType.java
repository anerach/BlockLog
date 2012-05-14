package me.arno.blocklog.logs;

public enum LogType {
	BREAK(0),
	PLACE(1),
	FIRE(2),
	EXPLOSION(3),
	LEAVES(4),
	GROW(5),
	PORTAL(6),
	FORM(7),
	SPREAD(8),
	FADE(9),
	CREEPER(10),
	FIREBALL(11),
	TNT(12),
	CHAT(101),
	COMMAND(102),
	DEATH(103),
	KILL(104);
	
	int id;
	LogType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public LogType getType() {
		return this;
	}
}
