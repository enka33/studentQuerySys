package com.mec.scoresys.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mec.scoresys.action.TeacherManageAction;

/**
 * Servlet implementation class TeacherManage
 */
@WebServlet("/TeacherManage")
public class TeacherManage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TeacherManage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("utf-8");
		String buffer = "";
		BufferedReader bufferedReader = request.getReader();
		String str;
		while((str = bufferedReader.readLine()) != null) {
			buffer += str + "\n";
		}
		String data = URLDecoder.decode(buffer, "UTF-8");
		TeacherManageAction teacherManageAction = new TeacherManageAction(data);
		String returnStr = teacherManageAction.execute();
		PrintWriter out = response.getWriter();
		out.print(returnStr);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
