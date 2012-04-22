package me.arno.blocklog.database;


public class Query {
	private String selectClause;
	private String fromClause;
	private String whereClause;
	private String groupByClause;
	private String orderByClause;
	private String limitClause;
	
	public Query() {}
	
	public Query(String table) {
		fromClause = "FROM " + table;
	}
	
	public void addSelect(String select) {
		if(selectClause == null) {
			selectClause = "SELECT " + select;
		} else {
			selectClause += ", " + select;
		}
	}
	
	public void addSelectDate(String select) {
		addSelectDate(select, null);
	}

	public void addSelectDate(String select, String format) {
		String str;
		if(DatabaseSettings.DBType().equalsIgnoreCase("mysql")) {
			format = (format == null) ? "%d-%m-%Y %H:%i:%s" : format;
			str = "FROM_UNIXTIME(" + select + ", " + format + ")";
		} else {
			format = (format == null) ? "localtime" : format;
			str = "datetime(" + select + ", 'unixepoch', '" + format + "')";
		}
		
		if(selectClause == null) {
			selectClause = "SELECT " + str;
		} else {
			selectClause += ", " + str;
		}
	}
	
	public void addFrom(String from) {
		fromClause = "FROM " + from;
	}
	
	public void addGroupBy(String group) {
		if(groupByClause == null) {
			groupByClause = "GROUP BY " + group;
		} else {
			groupByClause += ", " + group;
		}
	}
	
	public void AddOrderBy(String order, String type) {
		orderByClause = "ORDER BY " + order + " " + type;
	}
	
	public void addLimit(Integer min) {
		addLimit(min, 0);
	}
	
	public void addLimit(Integer min, Integer max) {
		if(max == 0)
			limitClause = "LIMIT " + min;
		else
			limitClause = "LIMIT " + min + ", " + max;
	}
	
	public void addWhere(String column, String value) {
		addWhere(column, value, "=");
	}
	
	public void addWhere(String column, String value, String math) {
		addWhereClause("AND", column, value, math);
	}
	
	public void addOrWhere(String column, String value) {
		addOrWhere(column, value, "=");
	}
	
	public void addOrWhere(String column, String value, String math) {
		addWhereClause("OR", column, value, math);
	}
	
	private void addWhereClause(String type, String column, String value, String math) {
		if(whereClause == null) {
			whereClause = "WHERE " + column + math + "'" + value + "'";
		} else {
			whereClause += " " + type + " " + column + math + "'" + value + "'";
		}
	}
	
	public String getQuery() {
		String query = "";
		if(selectClause != null)
			query += selectClause;
		if(fromClause != null)
			query += " " + fromClause;
		if(whereClause != null)
			query += " " + whereClause;
		if(orderByClause != null)
			query += " " + orderByClause;
		if(limitClause != null)
			query += " " + limitClause;
		
		return query;
	}
}
