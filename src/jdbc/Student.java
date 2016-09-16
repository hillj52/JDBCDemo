package jdbc;

import java.util.ArrayList;

public class Student {

	private int id;
	private String firstName;
	private String lastName;
	private int sat;
	private double gpa;
	private int majorId = -1;
	private ArrayList<Integer> classes;
	private DBConnector dbc;
	
	public void enrollInClasses() {
		int classesToEnrollIn = 4;
		if(majorId!=-1) {
			this.classes = dbc.inMajorClasses(this.id);
			while(!classes.isEmpty() && classesToEnrollIn > 2) {
				dbc.enrollInClass(this.id,classes.get(0),true);
				classes.remove(0);
				classesToEnrollIn--;
			}
		}
		this.classes = dbc.outMajorClasses(id);
		while(classesToEnrollIn > 0 && !classes.isEmpty()) {
			dbc.enrollInClass(this.id,classes.get(0),false);
			classes.remove(0);
			classesToEnrollIn--;
		}
	}
	
	public void updateMajor(String newMajor) {
		this. majorId = this.dbc.updateMajor(this.id, this.sat, newMajor);
	}
	
	public void showClasses() {
		this.classes = dbc.inMajorClasses(id);
		System.out.println("In Major:");
		for (Integer classNum : classes) {
			System.out.println(classNum);
		}
		this.classes = dbc.outMajorClasses(id);
		System.out.println("Out of Major:");
		for (Integer classNum : classes) {
			System.out.println(classNum);
		}
	}
	
	public String toString() {
		return this.id + " " + this.fullName();
	}
	
	private String fullName() {
		return this.lastName + ", " + this.firstName;
	}
	
	public Student(String firstName, String lastName, int sat, double gpa) {
		this.dbc = DBConnector.getInstance();
		this.firstName = firstName;
		this.lastName = lastName;
		this.sat = sat;
		this.gpa = gpa;
		this.id = this.dbc.enrollStudent(firstName, lastName, sat, gpa);
	}
	
	public Student(String firstName, String lastName, int sat, double gpa, String major) {
		this(firstName,lastName,sat,gpa);
		this.updateMajor(major);
	}
}
