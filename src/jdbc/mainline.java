package jdbc;

import java.util.ArrayList;

public class Mainline {

	public static void main(String[] args) {
		DBConnector dbc = DBConnector.getInstance();
		
		dbc.enrollStudent("Adam", "Zapel", 1200, 3.0);
		dbc.updateMajor("Adam", "Zapel", "Finance");
//		dbc.signUpClasses("Adam","Zapel");
		
		dbc.enrollStudent("Graham", "Krakir", 500, 2.5);
		dbc.updateMajor("Graham", "Krakir", "General Studies");
//		dbc.signUpClasses("Graham","Krakir");
		
		dbc.enrollStudent("Ella", "Vader", 800, 3.0);
		dbc.updateMajor("Ella", "Vader", "Accounting");
//		dbc.signUpClasses("Adam","Zapel");
		
		dbc.enrollStudent("Stanley", "Kupp", 1350, 3.3);
		dbc.updateMajor("Stanley", "Kupp", "Accounting");
//		dbc.signUpClasses("Adam","Zapel");
		
		dbc.enrollStudent("Lou", "Zar", 950, 3.0);
		dbc.updateMajor("Lou", "Zar", "Education");
//		dbc.signUpClasses("Adam","Zapel");
		
//		JdbcConnector myJdbc = new JdbcConnector("tiy");
//		ArrayList<String> studentColumns = new ArrayList<String>();
//		ArrayList<String> majorColumns = new ArrayList<String>();
//		
//		studentColumns.add("first_name");
//		studentColumns.add("last_name");
//		studentColumns.add("gpa");
//		studentColumns.add("major_id");
//		studentColumns.add("sat_score");
//		
//		majorColumns.add("id");
//		majorColumns.add("description");
//		
//		//Insert new students record
//		myJdbc.execInsert("student (first_name,last_name,sat_score,gpa) values ('George','Washington',1600,4.0)");
//		
//		//Show that student has been inserted into database
//		myJdbc.execSelect("* from student where first_name = 'George' and last_name = 'Washington'");
//		myJdbc.printResults(studentColumns);
//		
//		//Update students record
//		myJdbc.execUpdate("student set sat_score = 1450, gpa = 3.5, major_id = " 
//				+ myJdbc.getIdOf("major","description","General Business") + 
//				" where first_name = 'George' and last_name = 'Washington'");
//		
//		//Show that students record has been properly updated
//		myJdbc.execSelect("* from student where first_name = 'George' and last_name = 'Washington'");
//		myJdbc.printResults(studentColumns);
//
//		//Delete new students record from database
//		myJdbc.execDelete("from student where last_name = 'Washington' and sat_score = 1450");
//		
//		//Show that record has been deleted
//		myJdbc.execSelect("* from student where first_name = 'George' and last_name = 'Washington'");
//		myJdbc.printResults(studentColumns);
//		
//		//Output backup() record
//		myJdbc.backup();
//		
//		//Prints MetaData about the Database
//		myJdbc.printMetaData();
//		
//		//Close database connection
//		myJdbc.close();
	}
}
