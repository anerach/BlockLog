package me.arno.blocklog.database;

import java.util.logging.Logger;

import me.arno.blocklog.BlockLog;

public class ConvertDatabase {
	String DBType; // MySQL, SQLite
	Logger log;
	
	public ConvertDatabase(BlockLog plugin, String DBType) {
		if(DBType == "MySQL" || DBType == "SQLite")
			this.DBType = DBType;
		else
			log.info("Incorrect DB Type: " + DBType);
		
		if(DBType == "MySQL")
			MySQLToSQLite();
		else if(DBType == "SQLite")
			SQLiteToMySQL();
			
	}
	
	public void MySQLToSQLite() {
		
	}
	
	public void SQLiteToMySQL() {
		
	}
}
