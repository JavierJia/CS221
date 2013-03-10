package server;

public class EngineRun {
	public static void main(String[] args) {
		EngineServer app = new EngineServer(new EngineServlet());
		app.start(8888);
		System.out.println("let me in");
		app.join();
		System.exit(0);
	}
}
