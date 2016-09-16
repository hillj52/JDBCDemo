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
	private static String ENROLL_IN_CLASS = "insert student_class_relationship (student_id,class_id) values (?,?)";
	private static String UPDATE_MAJOR = "update student set major_id = ? where id = ?";
	private static String FIND_MAJOR = "select * from major where description = ?";
	private static String MAJORS_OPEN = "select description from major where req_sat <= ?";
	private static String CLASSES_IN_MAJOR = "select class.id from "
			+ "student join major, major_class_relationship, class "
			+ "where student.id = ? "
			+ "and student.major_id = major.id and major.id = major_class_relationship.major_id "
			+ "and major_class_relationship.class_id = class.id";
	private static String ALL_CLASSES = "select class.id from class";
	private static String LAST_ID = "select last_insert_id()";
	private static String FULL_NAME = "select concat(last_name,', ',first_name) from student where id = ?";
	
	private static StudentTransLog log;
	
	public static DBConnector getInstance() {
		if (DBConnector.instance == null) 
			DBConnector.instance =  new DBConnector();
		return DBConnector.instance;

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
	 *
	 * delete from [table] where [conditions]
	 *	
	 * insert [table] [columns] values [values]
	 *
	 * update [table] set [column names = new values] where [conditions]
	 */
	public String getLogString() {
		return DBConnector.log.toString();
	}
	
	public void enrollInClass(int studentId, int classId, boolean isInMajor) {
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(ENROLL_IN_CLASS);
			prepStmt.setInt(1, studentId);
			prepStmt.setInt(2, classId);
			prepStmt.executeUpdate();
			String sb = "Enrolled " + this.getFullName(studentId) + " in class: " + classId + " which is ";
			sb+=isInMajor?"in major":"not in major";
			DBConnector.log.add(studentId,sb);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
	}
	
	public int enrollStudent(String firstName, String lastName, int sat_score, double gpa) {
		int studentId = -1;
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(ENROLL);
			prepStmt.setString(1,firstName);
			prepStmt.setString(2,lastName);
			prepStmt.setInt(3,sat_score);
			prepStmt.setDouble(4,gpa);
			prepStmt.executeUpdate();
			this.prepStmt = this.createPreparedStatement(LAST_ID);
			ResultSet result = prepStmt.executeQuery();
			result.next();
			studentId = result.getInt(1);
			DBConnector.log.add(studentId,"Enrolled " + this.getFullName(studentId) + 
					" as a student at Ball So Hard University! Student Id: " + studentId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
		return studentId;
	}
	
	public int updateMajor(int studentId, int studentSat, String newMajor) {
		ArrayList<Integer> majorInfo = this.getMajorInfo(newMajor);
		if (studentSat >= majorInfo.get(1)) {
			try {
				this.connect();
				this.prepStmt = this.createPreparedStatement(UPDATE_MAJOR);
				prepStmt.setInt(1, majorInfo.get(0));
				prepStmt.setInt(2, studentId);
				prepStmt.executeUpdate();
				DBConnector.log.add(studentId,this.getFullName(studentId) + " has an sat score of " + studentSat);
				DBConnector.log.add(studentId, "Assigned " + this.getFullName(studentId) + " to the major " + newMajor + 
						" which requires an sat score of " + majorInfo.get(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				this.close();
			}
		} else {
			try {
				this.connect();
				DBConnector.log.add(studentId,this.getFullName(studentId) + " has an sat score of " + studentSat);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				this.close();
			}
			DBConnector.log.add(studentId,"Failed to assign student to the " + 
				newMajor + " major, due to a required Sat score of " + majorInfo.get(1));
			DBConnector.log.add(studentId,this.printOpenMajors(studentSat));
		}
		return majorInfo.get(0);
	}
	
	public String getTableLayouts() {
		return tables.toString();
	}
	
	public ArrayList<Integer> outMajorClasses(int studentId) {
		ArrayList<Integer> classes = this.getAllClasses();
		ArrayList<Integer> majorClasses = this.inMajorClasses(studentId);
		for (Integer classNum : majorClasses) {
			if (classes.contains(classNum))
				classes.remove(classes.indexOf(classNum));
		}
		return classes;
	}
	
	private ArrayList<Integer> getAllClasses() {
		ArrayList<Integer> classes = new ArrayList<Integer>();
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(ALL_CLASSES);
			ResultSet results = prepStmt.executeQuery();
			while (results.next()) {
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
	
	public ArrayList<Integer> inMajorClasses(int studentId) {
		ArrayList<Integer> classes = new ArrayList<Integer>();
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(CLASSES_IN_MAJOR);
			prepStmt.setInt(1, studentId);
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
	
	private String getFullName(int studentId) throws SQLException {
		this.prepStmt = this.createPreparedStatement(FULL_NAME);
		prepStmt.setInt(1, studentId);
		ResultSet results = prepStmt.executeQuery();
		results.next();
		return results.getString(1);
	}
	
	private String printOpenMajors(int sat_score) {
		String sb = "";
		try {
			this.connect();
			this.prepStmt = this.createPreparedStatement(MAJORS_OPEN);
			prepStmt.setInt(1, sat_score);
			ResultSet results = prepStmt.executeQuery();
			sb += "Available Majors with " + sat_score + " sat score:\n";
			while(results.next()) {
				sb += results.getString(1) + '\n';
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
		return sb;
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
		log = new StudentTransLog();
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
