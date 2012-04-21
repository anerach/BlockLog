package me.arno.blocklog;

public class Query {
	private String selectClause;
	private String fromClause;
	private String whereClause;
	private String groupByClause;
	private String orderByClause;
	
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
		
		return query;
	}
}
