package com.mec.scoresys.model;

import java.util.Map;

public class StudentPersonalInfo extends ErrorInfo {
	private String id;
	private String name;
	private String department;
	private String major;
	private String IDNumber;
	private String status;
	private Map<String, String> subjectMap;
	private Map<String, Object> scoreMap;
	
	public StudentPersonalInfo() {
	}
	
	public StudentPersonalInfo(String id, String name, String department, String major) {
		this.id = id;
		this.name = name;
		this.department = department;
		this.major = major;
	}

	public Map<String, Object> getScoreMap() {
		return scoreMap;
	}

	public void setScoreMap(Map<String, Object> scoreMap) {
		this.scoreMap = scoreMap;
	}

	public StudentPersonalInfo(String errorId, String errorMess) {
		this.setErrorId(errorId);
		this.setErrorMess(errorMess);
	}

	public StudentPersonalInfo(String id, String name, String department, String major, Map<String, String> subjectMap) {
		this.id = id;
		this.name = name;
		this.department = department;
		this.major = major;
		this.subjectMap = subjectMap;
	}

	public Map<String, String> getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(Map<String, String> subjectMap) {
		this.subjectMap = subjectMap;
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

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getIDNumber() {
		return IDNumber;
	}

	public void setIDNumber(String iDNumber) {
		IDNumber = iDNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
