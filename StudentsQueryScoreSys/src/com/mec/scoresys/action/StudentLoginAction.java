package com.mec.scoresys.action;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mec.scoresys.filter.IMecSession;
import com.mec.scoresys.model.StudentPersonalInfo;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import net.sf.json.JSONObject;

public class StudentLoginAction implements ILoginAction, IMecSession {
	private String id;
	private String password;
	private Map<String, String> sessionMap;
	private static StudentPersonalInfo studentInfo;
	
	public StudentLoginAction() {
	}

	public StudentLoginAction(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static StudentPersonalInfo getStudentInfo() {
		return studentInfo;
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
					 studentInfo = new StudentPersonalInfo("00002", "账号或密码错误!");
					 sessionMap.put("error", JSONObject.fromObject(studentInfo).toString());
					 return "ERROR";
				 } else if(statusSQL.equals("1")) {
					 studentInfo = new StudentPersonalInfo("00003", "你的账号不能用,是毕业了吗! (温馨建议:请与你导员联系(￣▽￣)");
					 sessionMap.put("error", JSONObject.fromObject(studentInfo).toString());
					 return "ERROR";
				 }else {
					 String nameSQL = resultSet.getString("user_name");
					 String departmentMajorId = id.substring(0, 2) + id.substring(4, 6);
					 
					 SQLString = "SELECT department_major_id, department_major_name FROM department_major_info WHERE department_major_id='" 
					 + departmentMajorId +"' OR department_major_id='" 
					 + id.substring(0, 2) + "'";
					 ps = (PreparedStatement) connection.prepareStatement(SQLString);
					 resultSet = ps.executeQuery();
					 String department = "";
					 String major = "";
					 while(resultSet.next()) {
						 String temp = resultSet.getString("department_major_id");
						 if(temp.length() == 2) {
							 department = resultSet.getString("department_major_name");
						 } else if(temp.length() == 4) {
							 major = resultSet.getString("department_major_name");
						 }
					 }
					 
					 studentInfo = new StudentPersonalInfo(id, nameSQL, department, major);
					 sessionMap.put(id, JSONObject.fromObject(studentInfo).toString());
					 
					 return "SUCCESS";
				 }
			 } else {
				 studentInfo = new StudentPersonalInfo("00001", "账号或密码错误!");
				 sessionMap.put("error", JSONObject.fromObject(studentInfo).toString());
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

	@Override
	public Map<String, String> getSessionMap() {
		return sessionMap;
	}
}
