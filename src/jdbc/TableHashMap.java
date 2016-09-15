package jdbc;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

public class TableHashMap {

	private HashMap<String,TableLayout> tables;
	private ArrayList<String> tableNames;
	private int numTables;
	
	public String toString() {
		String sb = "Displaying info for " + numTables + " tables:\n";
		for (String name : tableNames) {
			sb += tables.get(name).toString();
		}
		return sb;
	}
	
	public int getNumTables() {
		return this.numTables;
	}
	
	public void putTable(String tableName, ResultSetMetaData rsmd) {
		this.tableNames.add(tableName);
		this.tables.put(tableName, new TableLayout(tableName,rsmd));
		numTables++;
	}
	
	public TableLayout getTable(String tableName) {
		return this.tables.get(tableName);
	}
	
	public TableHashMap() {
		this.tables = new HashMap<String,TableLayout>();
		this.tableNames = new ArrayList<String>();
		this.numTables = 0;
	}
}
