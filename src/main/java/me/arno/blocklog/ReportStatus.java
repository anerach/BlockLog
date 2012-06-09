package me.arno.blocklog;

public enum ReportStatus {
	NEW(0),
	WAITING(1),
	PROCESSED(2),
	FIXED(3),
	INVALID(4),
	DUPLICATE(5);
	
	int id;
	ReportStatus(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
