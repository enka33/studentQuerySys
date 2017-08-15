package com.mec.scoresys.action;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.mec.scoresys.model.SubjectScoreInfo;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class StudentQueryScoreAction {
	private String queryData;
	private Map<String, Object> scoreMap;
	
	public StudentQueryScoreAction(String queryData) {
		this.queryData = queryData;
	}
	
	public void execute() {
		scoreMap = new HashMap<String, Object>();
		String[] queryArr = queryData.split("&");
		
		String id = " AND SUBSTRING(score_id,1,8)='" + queryArr[0].substring(queryArr[0].indexOf("=") + 1) + "'";
		String queryContent = queryArr[1].substring(queryArr[1].indexOf("=") + 1).equals("all") ? " AND (score_status='1' OR score_status='0')" : " AND score_status='1'";
		String year = queryArr[2].substring(queryArr[2].indexOf("=") + 1);
		year = dealYear(year);
		String term = queryArr[3].substring(queryArr[3].indexOf("=") + 1);
		term = dealTerm(term);
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			String scoreId = "";
			String score = "";
			String makeupScore = "";
			String scoreStatus = "";
			String subject = "";
			
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/score_query_sys", "root", "610121");
			String SQLString = "SELECT score_id, score, makeup_score, score_status FROM score_info"
					+ " WHERE TRUE" + id + queryContent + year + term;
			ps = (PreparedStatement)connection.prepareStatement(SQLString);
			resultSet = ps.executeQuery();
			while(resultSet.next()) {
				scoreId = resultSet.getString("score_id");
				score = resultSet.getString("score");
				makeupScore = resultSet.getString("makeup_score");
				scoreStatus = resultSet.getString("score_status");
				
				subject = querySubject(connection, ps, resultSet, scoreId);
				SubjectScoreInfo scoreInfo = new SubjectScoreInfo(score, makeupScore, scoreStatus, subject);
				scoreMap.put(scoreId, scoreInfo);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
				if(ps != null) {
					ps.close();
				}
				if(connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private String querySubject(Connection connection, PreparedStatement ps, ResultSet resultSet, String scoreId) throws SQLException {
		String subjectId = scoreId.substring(0, 2) + scoreId.substring(4, 6) + scoreId.substring(11);
		String SQLString = "SELECT subject_id, subject_name FROM subject_info"
				+ " WHERE subject_id='" + subjectId + "'";
		ps = (PreparedStatement)connection.prepareStatement(SQLString);
		resultSet = ps.executeQuery();
		String subjectName = "";
		if(resultSet.next()) {
			subjectName = resultSet.getString("subject_name");
		}
		return subjectName;
	}

	private String dealTerm(String term) {
		String temp = "";
		
		if(term.length() > 4){
			String a = term.substring(1, 2);
			temp = " AND SUBSTRING(score_id,11,1)='" + a + "'";
		} else {
			temp = " AND (SUBSTRING(score_id,11,1)='1' OR SUBSTRING(score_id,11,1)='2')";
		}
		return temp;
	}

	private String dealYear(String year) {
		String temp = "";
		if(year.equals("È«²¿")) {
			Calendar now = Calendar.getInstance();
			int currYear = now.get(Calendar.YEAR) % 2000;
			int currMonth = now.get(Calendar.MONTH) + 1;
			int startYear = 14;
			
			for(int i = startYear; i < currYear; i++) {
				if(i == currYear-1) {
					if(currMonth >= 2) {
						temp += " OR SUBSTRING(score_id,9,2)='" + i + "')";
					}
				} else {
					temp += i == startYear ? " AND (" : " OR";
					temp += i != startYear ? " SUBSTRING(score_id,9,2)='" + i + "'" : "SUBSTRING(score_id,9,2)='" + i + "'";
				}
			}
		} else {
			temp = " AND SUBSTRING(score_id,9,2)='" + year.substring(2, 4) + "'";
		}
		return temp;
	}

	public Map<String, Object> getScoreMap() {
		return scoreMap;
	}
}
