package jdbc;

import java.sql.*;
import java.util.ArrayList;

public class TableLayout {

	private static final String INVALID_MSG = "Invalid Column Number";
	
	private String name;
	private int numColumns;
	private ArrayList<String> columns;
	private ArrayList<String> columnTypes;
	private ArrayList<Integer> columnPrecisions;
	private ArrayList<Integer> columnWidths;
	private ArrayList<Boolean> isColumnAutoIncrement;
	
	public String toString() {
		String sb = this.name + '\n';
			for (int i=0;i<this.numColumns;i++) {
				sb += '\t' + columns.get(i) + ": " + columnTypes.get(i) + 
						"(" + columnPrecisions.get(i) + ")\n";
			}
		return sb;
	}
	
	public String getFormatString(int column) {
		if (isValidColumn(column))
			return "%-" + (columnWidths.get(column) + 3) + "s";
		else
			return "%-20s";
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColumnName(int column) {
		if(isValidColumn(column))
			return this.columns.get(column);
		else
			return INVALID_MSG;
	}
	
	public String getColumnType(int column) {
		if(isValidColumn(column))
			return this.columnTypes.get(column);
		else
			return INVALID_MSG;
	}
	
	public int getColumnPrecision(int column) {
		if(isValidColumn(column))
			return this.columnPrecisions.get(column);
		else
			return -1;
	}
	
	public int getColumnWidth(int column) {
		if(isValidColumn(column))
			return this.columnWidths.get(column);
		else
			return -1;
	}
	
	public boolean isColumnAutoIncrement(int column) {
		return isValidColumn(column)?this.isColumnAutoIncrement.get(column):false;
	}
	
	public ArrayList<String> getColumns() {
		return this.columns;
	}
	
	public ArrayList<String> getColumnTypes() {
		return this.columnTypes;
	}
	
	public ArrayList<Integer> getColumnPrecisions() {
		return this.columnPrecisions;
	}
	
	public ArrayList<Integer> getColumnWidths() {
		return this.columnWidths;
	}
	
	private boolean isValidColumn(int column) {
		return (column >= 0 && column < this.numColumns);
	}
	
	public TableLayout(String name, ResultSetMetaData rsmd) {
		this.name = name;
		this.columns = new ArrayList<String>();
		this.columnTypes = new ArrayList<String>();
		this.columnPrecisions = new ArrayList<Integer>();
		this.columnWidths = new ArrayList<Integer>();
		this.isColumnAutoIncrement = new ArrayList<Boolean>();
		try {
			this.numColumns = rsmd.getColumnCount();
			for (int i=1;i<=this.numColumns;i++) {
				columns.add(rsmd.getColumnLabel(i)); 
				columnTypes.add(rsmd.getColumnTypeName(i));
				columnPrecisions.add(rsmd.getPrecision(i)); 
				columnWidths.add(rsmd.getColumnDisplaySize(i));
				isColumnAutoIncrement.add(rsmd.isAutoIncrement(i));
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
