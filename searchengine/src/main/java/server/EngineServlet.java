package server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.lucene.queryparser.classic.ParseException;

import ranker.Searcher;

public class EngineServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Searcher searcher;
	public final static String INDEX_PATH = "/home/jianfeng/workspace/code/cs221/index"; 
	
	public EngineServlet(){
		try {
			searcher = new Searcher(INDEX_PATH,null, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String query = request.getParameter("query");
		PrintWriter out = response.getWriter();
		if (query == null){
			return;
		}
		JSONObject json = new JSONObject();
		json.put("query", query);
		JSONObject result = new JSONObject();
		int start = 0;
		String strStart = request.getParameter("start");
		if (strStart != null){
			try{
				start = Integer.parseInt(strStart);
			}
			catch(Exception ex){
				start = 0;
			}
		}
		try {
			 result = searcher.doWebPagingSearch(query, null, null, 10, start);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		json.put("result", result);
		out.println(json.toString(2));
	}

}
