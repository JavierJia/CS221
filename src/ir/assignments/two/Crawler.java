package ir.assignments.two;

import java.io.File;
import java.io.FileWriter;
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

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				//&& href.startsWith("http://www.ics.uci.edu/");
				&& SURFIX.matcher(href).matches();
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String title = htmlParseData.getTitle();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();

			if (LOGFILE != null) {
				writeToLog(url, title, text);
			}

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
		}
	}

	private synchronized void writeToLog(String url, String title, String text) {
		try {
			// write the data ...
			FileWriter fWriter = new FileWriter(LOGFILE, true);
			StringBuilder sb = new StringBuilder(url);
			sb.append("\n");
			sb.append(title);
			sb.append("\n");
			sb.append(text.replace(System.getProperty("line.separator")," "));
			sb.append("\n\n");
			fWriter.write(sb.toString());
			fWriter.flush();
			fWriter.close();
		} catch (Exception e) {
			System.err.println("Error when writing " + url );
			e.printStackTrace();
		}
	}

	public static void setLogFile(String filename) {
		LOGFILE = new File(filename);
	}

	public static String getLogFile() {
		return LOGFILE.getName();
	}

	private static File LOGFILE = null;
	public static void main(String[] args) throws Exception {
		System.out.println(SURFIX.matcher("http://www.ics.uci.edu/2382/djfj").matches()) ;
		System.out.println(SURFIX.matcher("http://djf.ics.uci.edu/").matches()) ;
		System.out.println(SURFIX.matcher("http://tomato.ics.uci.edu/").matches()) ;
		System.out.println(SURFIX.matcher("http://cs.uci.edu/").matches()) ;
	}
}
