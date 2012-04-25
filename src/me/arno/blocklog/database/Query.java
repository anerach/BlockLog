package me.arno.blocklog.database;


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
	
	public void addSelect(String... selects) {
		for(String select : selects) {
			if(selectClause == null) {
				selectClause = "SELECT " + select;
			} else {
				selectClause += ", " + select;
			}
		}
	}
	
	public void addSelectAs(String select, String as) {
		if(selectClause == null) {
			selectClause = "SELECT " + select + " AS " + as;
		} else {
			selectClause += ", " + select + " AS " + as;
		}
	}
	
	public void addSelectDate(String select) {
		addSelectDate(select, null, null);
	}
	
	public void addSelectDateAs(String select, String as) {
		addSelectDate(select, null, as);
	}

	public void addSelectDate(String select, String format, String as) {
		format = (format == null) ? "%d-%m-%Y %H:%i:%s" : format;
		String str = "FROM_UNIXTIME(" + select + ", '" + format + "')" + (as == null ? "" : " AS " + as);
		
		if(selectClause == null) {
			selectClause = "SELECT " + str;
		} else {
			selectClause += ", " + str;
		}
	}
	
	public void addFrom(String from) {
		fromClause = "FROM " + from;
		table = from;
	}
	
	public void addGroupBy(String group) {
		if(groupByClause == null) {
			groupByClause = "GROUP BY " + group;
		} else {
			groupByClause += ", " + group;
		}
	}
	
	public void addOrderBy(String order) {
		addOrderBy(order, "ASC");
	}
	
	public void addOrderBy(String order, String type) {
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
	
	public void addLeftJoin(String joinedTable, String tableRow, String mTableRow) {
		addJoin("LEFT", joinedTable, tableRow, mTableRow);
	}
	
	public void addRightJoin(String joinedTable, String tableRow, String mTableRow) {
		addJoin("RIGHT", joinedTable, tableRow, mTableRow);
	}
	
	private void addJoin(String type, String joinedTable, String tableRow, String mTableRow) {
		String join = "";
		if(type != null) {
			join += type + " ";
		}
		joinClause = join + "JOIN " + joinedTable + " ON " + table + "." + tableRow + " = " + joinedTable + "." + mTableRow;
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
}
