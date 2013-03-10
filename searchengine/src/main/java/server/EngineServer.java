package server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class EngineServer {
	private Server server;
	private final HttpServlet servlet;

	public EngineServer(HttpServlet servlet){
		this.servlet = servlet;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello Servlet</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId());
    }
	
	public void start(int port) {
		server = new Server(port);
		try {
			HandlerList handlers = new HandlerList();
			handlers.addHandler(servletHandler());
			handlers.addHandler(new DefaultHandler());
			server.setHandler(handlers);
			server.start();
		} catch (Exception e) {
			shutdown();
			throw new RuntimeException(e);
		}
	}

	private void shutdown() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	private Handler servletHandler() {
		ServletHandler servletHandler = new ServletHandler();
		servletHandler.addServletWithMapping(new ServletHolder(servlet), "/search");
		return servletHandler;
	}
	
	public void join() {
		try {
			server.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
