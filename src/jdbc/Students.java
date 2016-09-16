package jdbc;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Students extends ArrayList<Student> {
	
	public String toString() 
	{
		String sb = "";
		for (Student s : this) {
			sb += s.toString();
		}
		return sb;
	}
	
	public Students() {
		super();
	}
}
