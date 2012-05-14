package me.arno.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import me.arno.blocklog.BlockLog;


public class Query {
	private String table;
	
	private String selectClause;
	private String fromClause;
	private String joinClause;
	private String whereClause;
	private String groupByClause;
	private String orderByClause;
	private String limitClause;
	
	public Query() {}
	
	public Query(String from) {
		fromClause = "FROM " + from;
		table = from;
	}
	
	public Query select(String... selects) {
		for(String select : selects) {
			if(selectClause == null)
				selectClause = "SELECT " + select;
			else
				selectClause += ", " + select;
		}
		return this;
	}
	
	public Query selectAs(String select, String as) {
		if(selectClause == null)
			selectClause = "SELECT " + select + " AS " + as;
		else
			selectClause += ", " + select + " AS " + as;
		return this;
	}
	
	public Query selectDate(String select) {
		return selectDate(select, null, select);
	}
	
	public Query selectDateAs(String select, String as) {
		return selectDate(select, null, as);
	}

	public Query selectDate(String select, String format) {
		return selectDate(select, format, select);
	}
	
	public Query selectDate(String select, String format, String as) {
		String defaultFormat = BlockLog.plugin.getSettingsManager().getDateFormat();
		format = (format == null) ? defaultFormat : format;
		String str = "FROM_UNIXTIME(" + select + ", '" + format + "')" + (as == null ? "" : " AS " + as);
		
		if(selectClause == null)
			selectClause = "SELECT " + str;
		else
			selectClause += ", " + str;
		return this;
	}
	
	public Query from(String from) {
		fromClause = "FROM " + from;
		table = from;
		return this;
	}
	
	public Query groupBy(String... groups) {
		for(String group : groups) {
			if(groupByClause == null)
				groupByClause = "GROUP BY " + group;
			else
				groupByClause += ", " + group;
		}
		return this;
	}
	
	public Query orderBy(String order) {
		return orderBy(order, "ASC");
	}
	
	public Query orderBy(String order, String type) {
		orderByClause = "ORDER BY " + order + " " + type;
		return this;
	}
	
	public Query limit(Integer min) {
		return limit(min, 0);
	}
	
	public Query limit(Integer min, Integer max) {
		if(max == 0)
			limitClause = "LIMIT " + min;
		else
			limitClause = "LIMIT " + min + ", " + max;
		return this;
	}
	
	public Query leftJoin(String joinedTable, String tableRow, String mTableRow) {
		return join("LEFT", joinedTable, tableRow, mTableRow);
	}
	
	public Query rightJoin(String joinedTable, String tableRow, String mTableRow) {
		return join("RIGHT", joinedTable, tableRow, mTableRow);
	}
	
	private Query join(String type, String joinedTable, String tableRow, String mTableRow) {
		String join = "";
		if(type != null) {
			join += type + " ";
		}
		joinClause = join + "JOIN " + joinedTable + " ON " + table + "." + tableRow + " = " + joinedTable + "." + mTableRow;
		return this;
	}
	
	public Query where(String column, Object value) {
		return where(column, value, "=");
	}
	
	public Query where(String column, Object value, String math) {
		return whereClause("AND", column, value, math);
	}
	
	public Query orWhere(String column, Object value) {
		return orWhere(column, value, "=");
	}
	
	public Query orWhere(String column, Object value, String math) {
		return whereClause("OR", column, value, math);
	}
	
	private Query whereClause(String type, String column, Object value, String math) {
		if(whereClause == null) {
			whereClause = "WHERE " + column + math + "'" + value.toString() + "'";
		} else {
			whereClause += " " + type + " " + column + math + "'" + value.toString() + "'";
		}
		return this;
	}
	
	private String getQuery() throws SQLException {
		String query = "";
		
		if(selectClause != null)
			query += selectClause;
		else
			throw new SQLException("SELECT clause can't be null");
		if(fromClause != null)
			query += " " + fromClause;
		else
			throw new SQLException("FROM clause can't be null");
		
		if(joinClause != null)
			query += " " + joinClause;
		if(whereClause != null)
			query += " " + whereClause;
		if(orderByClause != null)
			query += " " + orderByClause;
		if(limitClause != null)
			query += " " + limitClause;
		
		return query;
	}
	
	public int deleteRows() throws SQLException {
		String query = "DELETE";
		if(fromClause != null)
			query += " " + fromClause;
		else
			throw new SQLException("FROM clause can't be null");
		if(whereClause != null)
			query += " " + whereClause;
		
		Connection conn = BlockLog.plugin.conn;
		Statement stmt = conn.createStatement();
		
		return stmt.executeUpdate(query);
	}
	
	public ResultSet getResult() throws SQLException {
		Connection conn = BlockLog.plugin.conn;
		Statement stmt = conn.createStatement();
		
		return stmt.executeQuery(getQuery());
	}
	
	public Integer getRowCount() throws SQLException {
		Connection conn = BlockLog.plugin.conn;
		Statement stmt = conn.createStatement();
		
		String query = "SELECT COUNT(*) AS count";
		
		if(fromClause != null)
			query += " " + fromClause;
		else
			throw new SQLException("FROM clause can't be null");
		
		if(joinClause != null)
			query += " " + joinClause;
		if(whereClause != null)
			query += " " + whereClause;
		if(orderByClause != null)
			query += " " + orderByClause;
		if(limitClause != null)
			query += " " + limitClause;
		
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getInt("count");
	}
	
	public Integer sendUpdate(HashMap<String, String> rowsValues) throws SQLException {
		Connection conn = BlockLog.plugin.conn;
		Statement stmt = conn.createStatement();
		
		String rows = "";
		String values = "";
		
		boolean first = true;
		
		Set<Entry<String, String>> argSet = rowsValues.entrySet();
		for (Entry<String, String> arg : argSet) {
			rows += (first) ? "`" + arg.getKey() + "`" : ", `" + arg.getKey() + "`";
			values += (first) ? "'" + arg.getValue() + "'" : ", '" + arg.getValue() + "'";
			first = false;
	    }
		
		return stmt.executeUpdate("INSERT INTO " + table + " (" + rows + ") VALUES (" + values + ")");
	}
}
