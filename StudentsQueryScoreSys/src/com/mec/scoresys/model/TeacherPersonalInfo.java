package com.mec.scoresys.model;

import java.util.ArrayList;
import java.util.Map;

public class TeacherPersonalInfo extends ErrorInfo {
	private String id;
	private String name;
	private String department;
	private ArrayList<String> majorList;
	private Map<String, ArrayList<String>> majorSubjectMap;
	
	public TeacherPersonalInfo() {
	}
	
	public TeacherPersonalInfo(String errorId, String errorMess) {
		this.setErrorId(errorId);
		this.setErrorMess(errorMess);
	}

	public TeacherPersonalInfo(String id, String name, String department, ArrayList<String> majorList,
			Map<String, ArrayList<String>> majorSubjectMap) {
		this.id = id;
		this.name = name;
		this.department = department;
		this.majorList = majorList;
		this.majorSubjectMap = majorSubjectMap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public ArrayList<String> getMajorList() {
		return majorList;
	}

	public void setMajorList(ArrayList<String> majorList) {
		this.majorList = majorList;
	}

	public Map<String, ArrayList<String>> getMajorSubjectMap() {
		return majorSubjectMap;
	}

	public void setMajorSubjectMap(Map<String, ArrayList<String>> majorSubjectMap) {
		this.majorSubjectMap = majorSubjectMap;
	}
	
	
}
