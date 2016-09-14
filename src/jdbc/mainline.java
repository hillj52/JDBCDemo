package jdbc;

import java.util.ArrayList;

public class mainline {

	public static void main(String[] args) {
		JdbcConnector myJdbc = new JdbcConnector("tiy");
		ArrayList<String> studentColumns = new ArrayList<String>();
		ArrayList<String> majorColumns = new ArrayList<String>();
		
		studentColumns.add("first_name");
		studentColumns.add("last_name");
		studentColumns.add("gpa");
		studentColumns.add("major_id");
		studentColumns.add("sat_score");
		
		majorColumns.add("id");
		majorColumns.add("description");
		
		myJdbc.execInsert("student (first_name,last_name,sat_score,gpa) values ('George','Washington',1600,4.0)");
		myJdbc.execSelect("* from student where first_name = 'George' and last_name = 'Washington'");
		myJdbc.printResults(studentColumns);
		myJdbc.execUpdate("student set sat_score = 1450, gpa = 3.5, major_id = " 
				+ myJdbc.getIdOf("major","description","General Business") + 
				" where first_name = 'George' and last_name = 'Washington'");
		
		myJdbc.execSelect("* from student where first_name = 'George' and last_name = 'Washington'");
		myJdbc.printResults(studentColumns);

		myJdbc.execDelete("from student where last_name = 'Washington' and sat_score = 1450");
		myJdbc.execSelect("* from student where first_name = 'George' and last_name = 'Washington'");
		myJdbc.printResults(studentColumns);
		
		myJdbc.close();
	}
}
