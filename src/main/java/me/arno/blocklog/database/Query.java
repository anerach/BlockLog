package me.arno.blocklog.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import me.arno.blocklog.BlockLog;
import me.arno.blocklog.Config;


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
	
	public Query addSelect(String... selects) {
		for(String select : selects) {
			if(selectClause == null)
				selectClause = "SELECT " + select;
			else
				selectClause += ", " + select;
		}
		return this;
	}
	
	public Query addSelectAs(String select, String as) {
		if(selectClause == null)
			selectClause = "SELECT " + select + " AS " + as;
		else
			selectClause += ", " + select + " AS " + as;
		return this;
	}
	
	public Query addSelectDate(String select) {
		return addSelectDate(select, null, select);
	}
	
	public Query addSelectDateAs(String select, String as) {
		return addSelectDate(select, null, as);
	}

	public Query addSelectDate(String select, String format) {
		return addSelectDate(select, format, select);
	}
	
	public Query addSelectDate(String select, String format, String as) {
		String defaultFormat = new Config().getConfig().getString("blocklog.dateformat");
		format = (format == null) ? defaultFormat : format;
		String str = "FROM_UNIXTIME(" + select + ", '" + format + "')" + (as == null ? "" : " AS " + as);
		
		if(selectClause == null)
			selectClause = "SELECT " + str;
		else
			selectClause += ", " + str;
		return this;
	}
	
	public Query addFrom(String from) {
		fromClause = "FROM " + from;
		table = from;
		return this;
	}
	
	public Query addGroupBy(String... groups) {
		for(String group : groups) {
			if(groupByClause == null)
				groupByClause = "GROUP BY " + group;
			else
				groupByClause += ", " + group;
		}
		return this;
	}
	
	public Query addOrderBy(String order) {
		return addOrderBy(order, "ASC");
	}
	
	public Query addOrderBy(String order, String type) {
		orderByClause = "ORDER BY " + order + " " + type;
		return this;
	}
	
	public Query addLimit(Integer min) {
		return addLimit(min, 0);
	}
	
	public Query addLimit(Integer min, Integer max) {
		if(max == 0)
			limitClause = "LIMIT " + min;
		else
			limitClause = "LIMIT " + min + ", " + max;
		return this;
	}
	
	public Query addLeftJoin(String joinedTable, String tableRow, String mTableRow) {
		return addJoin("LEFT", joinedTable, tableRow, mTableRow);
	}
	
	public Query addRightJoin(String joinedTable, String tableRow, String mTableRow) {
		return addJoin("RIGHT", joinedTable, tableRow, mTableRow);
	}
	
	private Query addJoin(String type, String joinedTable, String tableRow, String mTableRow) {
		String join = "";
		if(type != null) {
			join += type + " ";
		}
		joinClause = join + "JOIN " + joinedTable + " ON " + table + "." + tableRow + " = " + joinedTable + "." + mTableRow;
		return this;
	}
	
	public Query addWhere(String column, Object value) {
		return addWhere(column, value, "=");
	}
	
	public Query addWhere(String column, Object value, String math) {
		return addWhereClause("AND", column, value, math);
	}
	
	public Query addOrWhere(String column, Object value) {
		return addOrWhere(column, value, "=");
	}
	
	public Query addOrWhere(String column, Object value, String math) {
		return addWhereClause("OR", column, value, math);
	}
	
	private Query addWhereClause(String type, String column, Object value, String math) {
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
	
	public ResultSet getResult() throws SQLException {
		Connection conn = BlockLog.plugin.conn;
		Statement stmt = conn.createStatement();
		
		return stmt.executeQuery(getQuery());
	}
	
	public Integer getRowCount() throws SQLException {
		Connection conn = BlockLog.plugin.conn;
		Statement stmt = conn.createStatement();
		
		selectClause = "SELECT COUNT(*) AS count";
		
		ResultSet rs = stmt.executeQuery(getQuery());
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
