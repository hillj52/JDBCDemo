package jdbc;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class StudentTransLog extends HashMap<Integer,ArrayList<String>> {

	private ArrayList<Integer> studentIds;

	public String toString() {
		String sb = "";
		for (Integer i : studentIds) {
			sb += "\nEducation System - Enrollment Process\n";
			sb += "=====================================\n";
			for (String msg : this.get(i)) {
				sb += msg + '\n';
			}
		}
		return sb;
	}
	
	public void add(Integer key, String message) {
		if (studentIds.contains(key)) {
			this.get(key).add(message);
		} else {
			studentIds.add(key);
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(message);
			this.put(key, temp);
		}
		
	}
	
	public ArrayList<String> put(Integer key, ArrayList<String> value) {
		return super.put(key, value);
	}
	
	public StudentTransLog() {
		super();
		this.studentIds = new ArrayList<Integer>();
	}
}
