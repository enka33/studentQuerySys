package com.mec.scoresys.filter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mec.scoresys.action.ILoginAction;

/**
 * Servlet Filter implementation class LoginDispatcher
 */
@WebFilter("/LoginDispatcher")
public class LoginDispatcher implements Filter {
	private static Map<String, Class<? extends ILoginAction>> loginActionMap;
	private static Map<String, Map<String, String>> actionResultMap;
	
	static {
		loginActionMap = new HashMap<String, Class<? extends ILoginAction>>();
    	actionResultMap = new HashMap<String, Map<String,String>>();
	}

    /**
     * Default constructor. 
     */
    public LoginDispatcher() {
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
		
		String uri = httpRequest.getRequestURI();
		if(!uri.substring(uri.lastIndexOf(".") + 1).equals("action")) {
			chain.doFilter(request, response);
			return;
		}
		String actionName = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
		try {
			String skipStr = dealAction(actionName, httpRequest);
			httpResponse.sendRedirect(skipStr);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
	}

	private String dealAction(String actionName, HttpServletRequest httpRequest) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Class<? extends ILoginAction> actionClass = loginActionMap.get(actionName);
		Object object = actionClass.newInstance();
		Method[] methods = actionClass.getDeclaredMethods();
		for(Method method : methods) {
			String methodStr = method.getName();
			if(methodStr.indexOf("set") != -1 
					&& method.getReturnType().equals(void.class) 
					&& method.getParameterTypes()[0].equals(String.class)) {
				String temp = methodStr.substring(3, 3+1).toLowerCase() + methodStr.substring(4);
				String arg = httpRequest.getParameter(temp);
				method.invoke(object, arg);
			}
		 }
		ILoginAction obj = (ILoginAction)object;
		String result = obj.execute();
		String skip = actionResultMap.get(actionName).get(result);
		
		IMecSession sessionObj = (IMecSession)object;
		HttpSession session = httpRequest.getSession();
		for(String key: sessionObj.getSessionMap().keySet()) {
			session.setAttribute(key, sessionObj.getSessionMap().get(key));
		}
		
		return skip + (result.equals("ERROR") ? "?error=error" : "?id=" + httpRequest.getParameter("id"));
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@SuppressWarnings("unchecked")
	public void init(FilterConfig fConfig) throws ServletException {
		InputStream is = LoginDispatcher.class.getResourceAsStream("./login_dispatcher.xml");
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			NodeList actionList = document.getElementsByTagName("action");
			for(int index = 0; index < actionList.getLength(); index++) {
				Element actionElement = (Element)actionList.item(index);
				String actionName = actionElement.getAttribute("name");
				String className = actionElement.getAttribute("class");
				
				Class<?> actionClass = Class.forName(className); 
				if(ILoginAction.class.isAssignableFrom(actionClass) && IMecSession.class.isAssignableFrom(actionClass)) {
					loginActionMap.put(actionName, (Class<? extends ILoginAction>)actionClass);
				}
				initActionResult(actionElement, actionName);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void initActionResult(Element actionElement, String actionName) {
		NodeList actionResultList = actionElement.getElementsByTagName("result");
		Map<String, String> resultMap = new HashMap<String, String>();
		for(int index = 0; index < actionResultList.getLength(); index++) {
				Element resultElement = (Element)actionResultList.item(index);
				String resultName = resultElement.getAttribute("name");
				String skipStr = resultElement.getTextContent();
				
				resultMap.put(resultName, skipStr);
		}
		actionResultMap.put(actionName, resultMap);
	}

}
