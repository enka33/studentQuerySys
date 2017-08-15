package com.mec.scoresys.model;

public class SubjectScoreInfo {
	private String name;
	private String score;
	private String makeupScore;
	private String status;
	private String subject;
	
	public SubjectScoreInfo() {
	}

	public SubjectScoreInfo(String score, String makeupScore, String status, String subject) {
		this.score = score;
		this.makeupScore = makeupScore;
		this.status = status;
		this.subject = subject;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getMakeupScore() {
		return makeupScore;
	}

	public void setMakeupScore(String makeupScore) {
		this.makeupScore = makeupScore;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
