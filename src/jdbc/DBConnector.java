package jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DBConnector {

	private static DBConnector instance = null;
	private static String ENROLL = "insert student (first_name,last_name,sat,gpa) values (?,?,?,?)";
	private static String UPDATE_MAJOR = "update student set major_id = ? where first_name = ? and last_name = ?";
	private static String FIND_MAJOR = "select * from major where description = ?";
	private static String SELECT_SAT = "select sat from student where first_name = ? and last_name = ?";
	private static String MAJORS_OPEN = "select description from major where req_sat <= ?";
	private static String CLASSES_IN_MAJOR = "select class.id from "
			+ "student join major, major_class_relationship, class "
			+ "where student.first_name = ? and student.last_name = ? "
			+ "and student.major_id = major.id and major.id = major_class_relationship.major_id "
			+ "and major_class_relationship.class_id = class.id";
	
	public static DBConnector getInstance() {
		return (DBConnector.instance == null)?new DBConnector():DBConnector.instance;
	}
	
	private String userName;
	private String password;
	private String dbUrl;
	private String dbOptions;
	private String dbName;
 	private Connection myConn = null;
	private Statement stmt = null;
	private PreparedStatement prepStmt = null;
	private TableHashMap tables = null;
	
	/*
	 * select [column names or *] from [table] where [conditions] 	 
	 */
	/*
	 * delete from [table] where [conditions]
	 */
	/*
	 * insert [table] [columns] values [values]
	 */
	/*
	 * update [table] set [column names = new values] where [conditions]
	 */

	public int enrollStudent(String firstName, String lastName, int sat_score, double gpa) {
		int retVal = 0;
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(ENROLL);
			prepStmt.setString(1,firstName);
			prepStmt.setString(2,lastName);
			prepStmt.setInt(3,sat_score);
			prepStmt.setDouble(4,gpa);
			retVal = prepStmt.executeUpdate();
			System.out.println("Enrolled " + this.getFullName(firstName, lastName) + " as a new student.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
		return retVal;
	}
	
	public int updateMajor(String firstName, String lastName, String newMajor) {
		ArrayList<Integer> majorInfo = this.getMajorInfo(newMajor);
		int studentSat = this.getSat(firstName, lastName);
		int retVal = 0;
		System.out.println(this.getFullName(firstName, lastName) + " has an sat score of " + studentSat + ".");
		if (studentSat >= majorInfo.get(1)) {
			try {
				this.connect();
				this.prepStmt = this.createPreparedStatement(UPDATE_MAJOR);
				prepStmt.setInt(1, majorInfo.get(0));
				prepStmt.setString(2, firstName);
				prepStmt.setString(3, lastName);
				retVal = prepStmt.executeUpdate();
				System.out.println("Assigned " + this.getFullName(firstName, lastName) + " to the " + 
				newMajor + " which requires an Sat score of " + majorInfo.get(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				this.close();
			}
		} else {
			System.out.println("Failed to assign " + this.getFullName(firstName, lastName) + " to the " + 
				newMajor + " due to a required Sat score of " + majorInfo.get(1));
			this.printOpenMajors(studentSat);
		}
		return retVal;
	}
	
	public String getTableLayouts() {
		return tables.toString();
	}
	
	public ArrayList<Integer> inMajorClasses(String firstName, String lastName) {
		ArrayList<Integer> classes = new ArrayList<Integer>();
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(CLASSES_IN_MAJOR);
			prepStmt.setString(1, firstName);
			prepStmt.setString(2, lastName);
			ResultSet results = prepStmt.executeQuery();
			while(results.next()) {
				classes.add(results.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
		return classes;
	}
	
	private String getFullName(String firstName, String lastName) {
		return lastName + ", " + firstName;
	}
	
	private void printOpenMajors(int sat_score) {
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(MAJORS_OPEN);
			prepStmt.setInt(1, sat_score);
			ResultSet results = prepStmt.executeQuery();
			System.out.println("Available Majors with " + sat_score + " sat score:"
					);
			while(results.next()) {
				System.out.println(results.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
	}
	
	private int getSat (String firstName, String lastName) {
		int retVal = -1;
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(SELECT_SAT);
			prepStmt.setString(1, firstName);
			prepStmt.setString(2, lastName);
			ResultSet result = prepStmt.executeQuery();
			result.next();
			retVal = result.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
		return retVal;
	}
	
	private ArrayList<Integer> getMajorInfo(String majorDescription) {
		ArrayList<Integer> retVals = new ArrayList<Integer>();
		retVals.add(-1);
		retVals.add(0);
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(FIND_MAJOR);
			prepStmt.setString(1,majorDescription);
			ResultSet result = prepStmt.executeQuery();
			result.next();
			retVals.set(0,result.getInt(1));
			retVals.set(1,result.getInt(3));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
		return retVals;
	}
	
	private PreparedStatement createPreparedStatement(String sql) throws SQLException {
		return myConn.prepareStatement(sql);
	}
	
	private void initHashMap() {
		tables = new TableHashMap();
		DatabaseMetaData dbmd = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			this.connect();
			dbmd = myConn.getMetaData();
			rs1 = dbmd.getTables(null, null, null, null);
			while (rs1.next()) {
				String tableName = rs1.getString("TABLE_NAME");
				stmt = myConn.createStatement();
				stmt.executeQuery("select * from " + tableName);
				rs2 = stmt.getResultSet();
				tables.putTable(tableName,rs2.getMetaData());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
	}
	
	private void connect() throws SQLException {
		this.myConn = DriverManager.getConnection(this.dbUrl + dbName + this.dbOptions,this.userName,this.password);
		this.stmt = myConn.createStatement();
	}
	
	private void close() {
		try {
			this.myConn.close();
			this.stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private DBConnector() {
		InputStream input = null;
		Properties props = new Properties();
		try {
			input = new FileInputStream("demo.properties");
			props.load(input);
			
			this.userName = props.getProperty("user");
			this.password = props.getProperty("password");
			this.dbUrl = props.getProperty("url");
			this.dbOptions = props.getProperty("options");
			this.dbName = props.getProperty("dbname");
			
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
		this.initHashMap();
	}
}
