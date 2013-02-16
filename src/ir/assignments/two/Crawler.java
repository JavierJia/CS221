package ir.assignments.two;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
	/**
	 * This method is for testing purposes only. It does not need to be used to
	 * answer any of the questions in the assignment. However, it must function
	 * as specified so that your crawler can be verified programatically.
	 * 
	 * This methods performs a crawl starting at the specified seed URL. Returns
	 * a collection containing all URLs visited during the crawl.
	 */
	public static Collection<String> crawl(String seedURL) {
		// TODO implement me
		return null;
	}

	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private final static Pattern SURFIX = Pattern.compile("^http://.*\\.ics\\.uci\\.edu/.*");
	private final static Pattern SKIPSITE = Pattern.compile("^http://(ftp|fano|kdd)\\.ics\\.uci\\.edu/.*");
	
	//skip URLs containing certain characters as probable queries, etc.
	private final static Pattern QUERRFILTERS = Pattern
			.compile(".*[\\?@=].*");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& !QUERRFILTERS.matcher(href).matches()
				&& SURFIX.matcher(href).matches()
				&& !SKIPSITE.matcher(href).matches();
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		WebURL url = page.getWebURL();

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String title = htmlParseData.getTitle();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			if (LOGPATH != null) {
				writeToLog(url, title, text);
			}
			if (LINKFILE != null){
				printLog(url,text,html,links);
			}
		}
	}

	private synchronized void printLog(WebURL url, final String text, final String html, final List<WebURL> links){
		System.out.println("URL: " + url.getURL());
		System.out.println("Text length: " + text.length());
		System.out.println("Html length: " + html.length());
		System.out.println("Number of outgoing links: " + links.size());
		try {
			FileWriter fWriter = new FileWriter(LINKFILE,true);
			StringBuilder builder = new StringBuilder(url.getURL());
			builder.append("\t");
			for ( WebURL link : links){
				builder.append(link.getURL());
				builder.append(" ");
			}
			builder.append("\n");
			fWriter.write(builder.toString());
			fWriter.close();
		} catch (IOException e) {
			System.err.println("Error when writing " + url.getURL());
			e.printStackTrace();
		}
	}
	
	private void writeToLog(WebURL url, String title, String text) {
		try {
			// write the data ...
			String path = url.getURL().substring("http://".length());
			File file = new File(LOGPATH + path);
			if (file.isDirectory()){
				file = new File(LOGPATH + path + "default.txt");
			}else if (url.getPath().indexOf(".") <0){
				file = new File(LOGPATH + path + "/default.txt");
			}
			if (!file.exists()){
				File parent = new File(file.getParent());
				if (!parent.exists()){
					parent.mkdirs();
				}
			}
			FileWriter fWriter = new FileWriter(file);
			fWriter.write(title);
			fWriter.write(text);
			fWriter.close();
		} catch (Exception e) {
			System.err.println("Error when writing " + url );
			e.printStackTrace();
		}
	}

	public static void setLogFile(String pathname) {
		LOGPATH = pathname;
	}
	
	public static void setLinkFile(String pathname){
		LINKFILE = pathname;
	}

	private static String LOGPATH = null;
	private static String LINKFILE = null;
	public static void main(String[] args) throws Exception {
		System.out.println(QUERRFILTERS.matcher("http://www.ics.uci.edu/2323??82/djfj?").matches());
		System.out.println(SURFIX.matcher("http://djf.ics.uci.edu/").matches()) ;
		System.out.println(SURFIX.matcher("http://tomato.ics.uci.edu/").matches()) ;
		System.out.println(SURFIX.matcher("http://cs.uci.edu/").matches()) ;
		System.out.println(SKIPSITE.matcher("http://kdd.ics.uci.edu/").matches());
		System.out.println("http://ics".substring("http://".length()));
	}
}
