package ir.assignments.three;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.parser.*;
import edu.uci.ics.crawler4j.url.*;

import java.io.*;

public class Crawler extends WebCrawler{
	
	// 
	private static FileWriter fstream;
	private static BufferedWriter bw;
	
	public Crawler(){
		super();
		try{
			fstream = new FileWriter("data.txt");
			bw = new BufferedWriter(fstream);
		}
		catch(Exception e){
			System.err.println("Open Error: " + e.toString());
		}
	}

	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 * 
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 */
	public static Collection<String> crawl(String seedURL) {
		// TODO implement me
		
		return null;
	}
	
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" 
             + "|png|tiff?|mid|mp2|mp3|mp4"
             + "|wav|avi|mov|mpeg|ram|m4v|pdf" 
             + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	/**
	* You should implement this function to specify whether
	* the given url should be crawled or not (based on your
	* crawling logic).
	*/
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
	}

	/**
	* This function is called when a page is fetched and ready 
	* to be processed by your program.
	*/
	@Override
	public void visit(Page page) {          
		String url = page.getWebURL().getURL();
		//System.out.println("URL: " + url);
		
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			List<WebURL> links = htmlParseData.getOutgoingUrls();
			
			/*System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());*/
			
			// write to data.txt
			//synchronized{
				try{
					bw.write(url + '\n'
				
						+ text.replace('\n', ' ') + '\n');
				}
				catch(Exception e){
					System.err.println("Write Error: " + e.toString());
				}
			//}
		}
	}
}
