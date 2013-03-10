package server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class EngineServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("into the servlet");
		PrintWriter out = resp.getWriter();
		out.println("Welcome to our site");
//		super.doGet(req, resp);
	}


	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("into the service");
		String action = request.getParameter("action");
		if (action != null){
		System.out.println(action);
		}else{
			System.out.println("action is null");
		}
		String query = request.getParameter("query");

		if (action != null) {
			if ("query".equals(action)){
				
			}
			else if ( "next".equals(action)){
				
			}else{
				
			}
		}

		response.getWriter().write("into the query");
		super.service(request, response);
	}
}
