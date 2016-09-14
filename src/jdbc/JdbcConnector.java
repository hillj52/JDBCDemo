package jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class JdbcConnector {

	private String userName;
	private String password;
	private String dbUrl;
	private String dbOptions;
	private String dbName;
 	private Connection myConn = null;
	private Statement stmt = null;
	
	public String getDbName() {
		return this.dbName;
	}
	
	public String getIdOf(String table, String column, String value) {
		this.execSelect("id, " + column + " from " + table);
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("id");
		columns.add(column);
		ArrayList<ArrayList<String>> temp = this.getResults(columns);
		for (ArrayList<String> record : temp) {
			if (record.get(1).equals(value))
				return record.get(0);
		}
		return null;
	}

	public void close() {
		try {
			if (myConn!=null && !myConn.isClosed()) {
				myConn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (stmt!=null && !stmt.isClosed()) {
				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void printResults(ArrayList<String> columns) {
		ArrayList<ArrayList<String>> resultList = this.getResults(columns);
		if (resultList.isEmpty()) {
			System.out.println("No Results Found");
		} else {
			String sb = "";
			for (String title : columns) {
				sb+= String.format("%-20s", title);
			}
			System.out.println(sb);
			for (ArrayList<String> record : resultList) {
				sb = "";
				for (String value : record) {
					sb += String.format("%-20s", value);
				}
				System.out.println(sb);
			}
		}
		System.out.println();
	}
	
	public ArrayList<ArrayList<String>> getResults(ArrayList<String> columns) {
		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
		ResultSet results = null;
		try {
			results = stmt.getResultSet();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		try {
			while (results.next()) {
				ArrayList<String> temp = new ArrayList<String>();
				for(String colName : columns) {
					String res = results.getString(colName);
					temp.add((res==null)?"None Provided":res);
				}
				resultList.add(temp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultList;
	}
	/*
	 * delete from [table] where [conditions]
	 */
	public int execDelete(String sqlStatement) {
		return this.execIUD("delete "+sqlStatement);
	}
	
	/*
	 * insert [table] [columns] values [values]
	 */
	public int execInsert(String sqlStatement) {
		return this.execIUD("insert "+sqlStatement);
	}
	
	/*
	 * update [table] set [column names = new values] where [conditions]
	 */
	public int execUpdate(String sqlStatement) {
		return this.execIUD("update "+sqlStatement);
	}
	
	
	private int execIUD(String sqlStatement) {
		try {
			stmt = myConn.createStatement();
			return stmt.executeUpdate(sqlStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * select [column names or *] from [table] where [conditions] 	 
	 */
	public void execSelect(String sqlStatement) {
		try {
			stmt = myConn.createStatement();
			stmt.executeQuery("select " + sqlStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JdbcConnector(String dbName) {
		this.dbName = dbName;
		InputStream input = null;
		Properties props = new Properties();
		try {
			input = new FileInputStream("demo.properties");
			props.load(input);
			
			this.userName = props.getProperty("user");
			this.password = props.getProperty("password");
			this.dbUrl = props.getProperty("url");
			this.dbOptions = props.getProperty("options");
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			this.myConn = DriverManager.getConnection(this.dbUrl + dbName + this.dbOptions,this.userName,this.password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
