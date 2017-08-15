package com.mec.scoresys.action;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mec.scoresys.filter.IMecSession;
import com.mec.scoresys.model.TeacherPersonalInfo;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import net.sf.json.JSONObject;

public class TeacherLoginAction implements ILoginAction, IMecSession {
	private String id;
	private String password;
	private Map<String, String> sessionMap;
	private static TeacherPersonalInfo teacherInfo;
	
	public TeacherLoginAction() {
	}
	
	public TeacherLoginAction(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Map<String, String> getSessionMap() {
		return sessionMap;
	}

	@SuppressWarnings("resource")
	@Override
	public String execute() {
		sessionMap = new HashMap<String, String>();
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/score_query_sys", "root", "610121");
			String SQLString = "SELECT user_id, password, user_name, user_status FROM user_info WHERE user_id='" + id + "'";
			ps = (PreparedStatement) connection.prepareStatement(SQLString);
			resultSet = ps.executeQuery();
			if(resultSet.next()) {
				 String passwordSQL = resultSet.getString("password");
				 String statusSQL = resultSet.getString("user_status");
				 if(!password.equals(passwordSQL)) {
					 teacherInfo = new TeacherPersonalInfo("00002", "’À∫≈ªÚ√‹¬Î¥ÌŒÛ!");
					 sessionMap.put("error", JSONObject.fromObject(teacherInfo).toString());
					 return "ERROR";
				 } else if(statusSQL.equals("1")) {
					 teacherInfo = new TeacherPersonalInfo("00003", "ƒ„µƒ’À∫≈π˝∆⁄! (Œ¬‹∞Ω®“È:«Î”Îƒ„…œ≤„¡™œµ(£˛®å£˛)");
					 sessionMap.put("error", JSONObject.fromObject(teacherInfo).toString());
					 return "ERROR";
				 } else {
					 String nameSQL = resultSet.getString("user_name");
					 String departmentId = id.substring(0, 2);
					 
					 SQLString = "SELECT department_major_id, department_major_name FROM department_major_info WHERE LEFT(department_major_id,2)='" 
					 + departmentId + "'";
					 ps = (PreparedStatement) connection.prepareStatement(SQLString);
					 resultSet = ps.executeQuery();
					 String department = "";
					 String major = "";
					 ArrayList<String> majorList = new ArrayList<String>();
					 Map<String, ArrayList<String>> majorSubjectMap = new HashMap<String, ArrayList<String>>();
					 while(resultSet.next()) {
						 String temp = resultSet.getString("department_major_id");
						 if(temp.length() == 2) {
							 department = resultSet.getString("department_major_name");
						 } else if(temp.length() == 4) {
							 major = resultSet.getString("department_major_name");
							 majorList.add(major + "(" + temp.substring(2) + ")");
							 ArrayList<String> majorSubjectList = dealMajorSubject(temp, connection, ps, resultSet);
							 majorSubjectMap.put(major, majorSubjectList);
						 }
					 }
					 
					 teacherInfo = new TeacherPersonalInfo(id, nameSQL, department, majorList, majorSubjectMap);
					 sessionMap.put(id, JSONObject.fromObject(teacherInfo).toString());
					 
					 return "SUCCESS";
				 }
			 } else {
				 teacherInfo = new TeacherPersonalInfo("00001", "’À∫≈ªÚ√‹¬Î¥ÌŒÛ!");
				 sessionMap.put("error", JSONObject.fromObject(teacherInfo).toString());
				 return "ERROR";
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
		return null;
	}

	private ArrayList<String> dealMajorSubject(String majorId, Connection connection, PreparedStatement ps,
			ResultSet resultSet) throws SQLException {
		ArrayList<String> subjectList = new ArrayList<String>();
		String SQLString = "SELECT subject_id, subject_name FROM subject_info WHERE LEFT(subject_id, 4)='" 
				 + majorId + "'";
		ps = (PreparedStatement) connection.prepareStatement(SQLString);
		resultSet = ps.executeQuery();
		while(resultSet.next()) {
			String subject = resultSet.getString("subject_name");
			String subjectId = resultSet.getString("subject_id");
			subjectList.add(subject + "(" + subjectId.substring(4) + ")");
		}
		return subjectList;
	}

}
