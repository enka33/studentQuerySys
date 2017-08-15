package com.mec.scoresys.model;

public class ErrorInfo {
	private String errorId;
	private String errorMess;
	
	public ErrorInfo() {
	}

	public ErrorInfo(String errorId, String errorMess) {
		this.errorId = errorId;
		this.errorMess = errorMess;
	}

	public String getErrorId() {
		return errorId;
	}

	public void setErrorId(String errorId) {
		this.errorId = errorId;
	}

	public String getErrorMess() {
		return errorMess;
	}

	public void setErrorMess(String errorMess) {
		this.errorMess = errorMess;
	}

}
