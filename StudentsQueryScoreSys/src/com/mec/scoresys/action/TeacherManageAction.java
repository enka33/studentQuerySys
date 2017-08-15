package com.mec.scoresys.action;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mec.scoresys.model.StudentPersonalInfo;
import com.mec.scoresys.model.SubjectScoreInfo;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TeacherManageAction {
	private String data;
	
	public TeacherManageAction() {
	}
	
	public TeacherManageAction(String data) {
		this.data = data;
	}
	
	public String execute() {
		String[] tempArr = data.split("=@");
		String id = tempArr[0].substring(tempArr[0].indexOf("=") + 1, tempArr[0].indexOf("&"));
		String[] dataArr = tempArr[1].substring(0, tempArr[1].indexOf("@")).split("&");
		String queryText = dataArr[0].substring(dataArr[0].indexOf("=") + 1);
		String returnStr = "";
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/score_query_sys", "root", "610121");
			if(queryText.equals("queryStudents")) {//传回对象数组
				returnStr = dealQureyStudens(id, dataArr, connection, ps, resultSet);
			} else if(queryText.equals("querySubject")) {//数组
				returnStr = dealSubject(id, dataArr, connection, ps, resultSet);
			} else if(queryText.equals("queryScore")) {//对象数组
				returnStr = dealScore(id, dataArr, connection, ps, resultSet);
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
		
		return returnStr;
	}

	private String dealScore(String id, String[] dataArr, Connection connection, PreparedStatement ps,
			ResultSet resultSet) throws SQLException {
		Map<String, Object> scoreMap = new HashMap<String, Object>();
		String json = "";
		String departmentId = id.substring(0, 2);
		String grade = dataArr[1].substring(dataArr[1].indexOf("0") + 1, dataArr[1].indexOf("级"));
		String major = dataArr[2].substring(dataArr[2].indexOf("=") + 1);
		String majorId = major.substring(major.indexOf("(") + 1, major.indexOf(")"));
		String subject = dataArr[3].substring(dataArr[3].indexOf("=") + 1);
		String subjectId = subject.substring(subject.indexOf("(") + 1, subject.indexOf(")"));
		String SQLString = "SELECT score_id, score, makeup_score FROM score_info"
				+ " WHERE score_id LIKE '"
				+ departmentId + grade + majorId + "%' AND score_id LIKE '%"
						+ subjectId + "'"; //AND RIGHT(score_id, 2)='" + subjectId + "'";
		ps = (PreparedStatement) connection.prepareStatement(SQLString);
		resultSet = ps.executeQuery();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		while(resultSet.next()) {
			SubjectScoreInfo scoreInfo = new SubjectScoreInfo();
			String scoreId = resultSet.getString("score_id");
			String score = resultSet.getString("score");
			scoreInfo.setScore(score);
			String makeupScore = resultSet.getString("makeup_score");
			scoreInfo.setMakeupScore(makeupScore);
			
			String studentId = scoreId.substring(0, 8);
			SQLString = "SELECT user_id, user_name FROM user_info"
					+ " WHERE user_id='" + studentId + "'";
			preparedStatement = (PreparedStatement) connection.prepareStatement(SQLString);
			rs = preparedStatement.executeQuery();
			if(rs.next()) {
				String studentName = rs.getString("user_name");
				scoreInfo.setName(studentName);
				scoreMap.put(studentId, scoreInfo);
			}
		}
		if(preparedStatement != null) {
			preparedStatement.close();
		}
		if(rs != null) {
			rs.close();
		}
		json = JSONObject.fromObject(scoreMap).toString();
		return json;
	}

	private String dealSubject(String id, String[] dataArr, Connection connection, PreparedStatement ps,
			ResultSet resultSet) throws SQLException {
		Map<String, String> subjectMap = new HashMap<String, String>();
		String json = "";
		String departmentId = id.substring(0, 2);
		String major = dataArr[1].substring(dataArr[1].indexOf("=") + 1);
		String majorId = major.substring(major.indexOf("(") + 1, major.indexOf(")"));
		String SQLString = "SELECT subject_id, subject_name FROM subject_info"
				+ " WHERE LEFT(subject_id, 4)='" + departmentId + majorId + "'";
		ps = (PreparedStatement) connection.prepareStatement(SQLString);
		resultSet = ps.executeQuery();
		while(resultSet.next()) {
			String subjectId = resultSet.getString("subject_id");
			String subjectName = resultSet.getString("subject_name");
			subjectMap.put(subjectId, subjectName);
		}
		json = JSONObject.fromObject(subjectMap).toString();
		return json;
	}

	private String dealQureyStudens(String id, String[] dataArr, Connection connection, PreparedStatement ps, ResultSet resultSet) throws SQLException {
		ArrayList<Object> studentsInfoList = new ArrayList<Object>();
		String json = "";
		String departmentId = id.substring(0, 2);
		String gradeId = dataArr[1].substring(dataArr[1].indexOf("=") + 1).substring(2, 4);
		String major = dataArr[2].substring(dataArr[2].indexOf("=") + 1);
		String majorId = major.substring(major.indexOf("(") + 1, major.indexOf(")"));
		String SQLString = "SELECT user_id, user_name, user_status, ID_number FROM user_info"
				+ " WHERE LEFT(user_id, 6)='" + departmentId + gradeId + majorId + "'";
		ps = (PreparedStatement) connection.prepareStatement(SQLString);
		resultSet = ps.executeQuery();
		while(resultSet.next()) {
			StudentPersonalInfo studentsInfo = new StudentPersonalInfo();
			String userId = resultSet.getString("user_id");
			studentsInfo.setId(userId);
			String name = resultSet.getString("user_name");
			studentsInfo.setName(name);
			String idNumder = resultSet.getString("ID_number");
			studentsInfo.setIDNumber(idNumder);
			String status = resultSet.getString("user_status");
			studentsInfo.setStatus(status);
			studentsInfoList.add(studentsInfo);
		}
		json = JSONArray.fromObject(studentsInfoList).toString();
		return json;
	}
}
