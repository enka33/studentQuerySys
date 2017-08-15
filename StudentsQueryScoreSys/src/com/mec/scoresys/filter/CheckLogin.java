package com.mec.scoresys.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class CheckLogin
 */
@WebFilter("/CheckLogin")
public class CheckLogin implements Filter {

    /**
     * Default constructor. 
     */
    public CheckLogin() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		httpRequest.setCharacterEncoding("utf-8");
		httpResponse.setCharacterEncoding("utf-8");
		String id = httpRequest.getParameter("id");
		HttpSession session = httpRequest.getSession();
		httpResponse.setContentType("text/json; charset=UTF-8");
		PrintWriter out = httpResponse.getWriter();
		if(session.getAttribute(id) == null) {
			out.print("{\"errorId\":\"004\",\"errorMess\":\"∑«∑®»Î«÷\"}");
			//response.sendRedirect("./Error.jsp?errId=004&errMess=Illegal Login!");
		} else {
			out.print(session.getAttribute(id));
		}
		out.close();
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
