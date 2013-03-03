package ranker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {
	
	/*
	 * rel[ 0, 1, ..., n - 1 ] corresponds to top 1, 2, ..., n relevances
	 * p indicate the P th DCG position 
	 */
	public static float DCG (float[] rel, int p) throws Exception{
		if (rel.length < p) {
			throw new Exception("invalid input! exit.");
		} else {
			float result = rel[0];
			while (p >= 2) {
				result += rel[p-1] * Math.log(2) / Math.log (p);
				p--;
			}
			return result;
		}
	}
	

	public static float NDCG (float[] rel, int p) throws Exception{
		float dcg = DCG(rel, p);
		float[] pRel = new float[p];
		for (int i = 0; i < p; i++) {
			pRel[i] = p - i;
		}
		
		for (float f: pRel) {
			System.out.println(f);
		}
		float idcg = DCG(pRel, p);
		return dcg/idcg;
	}
	
	/*
	 * 
	 */
	public static Map<String, Float> computePageRanks(String filePath) {
		// map<url, pageRank>
		Map<String, Float> pageRanks = new HashMap<String, Float>();
		
		// map<url1, url2>: str2 links to str1
		Map<String, List<String>> links = new HashMap<String, List<String>>();
		
		// map<url, outDegree>
		Map<String, Integer> outDegrees = new HashMap<String, Integer>();
		
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			// initiate links, pageRank, outDegree
			String line = br.readLine();
			while (line != null) {
				//process this line
				String[] subStrs = line.split(" +|\t");
				
				// set out degrees
				outDegrees.put(subStrs[0], subStrs.length - 1);
				//System.out.println("out degree: " + (subStrs.length - 1));
								
				for (int i = 1; i < subStrs.length; i++) {
					// initiate each page rank to 1
					pageRanks.put(subStrs[i], (float) 1);	
					
					// update links
					if (links.containsKey(subStrs[i])) {
						links.get(subStrs[i]).add(subStrs[0]);
						
					} else {
						List<String> al = new ArrayList<String>();
						al.add(subStrs[0]);
						links.put(subStrs[i], al);
					}
				}
				
				line = br.readLine();
			}
			br.close();
			fr.close();
			
		} catch (IOException e) {
			System.err.println(e.toString());
		}
		
		System.out.println("URL: " + "http://www.ics.uci.edu/~lopes/");
		System.out.print("Links: ");
		for (String s: links.get("http://www.ics.uci.edu/~lopes/")) {
			System.out.print(s + ' ');
		}
		System.out.print('\n');
		System.out.println("Out Degree: " + outDegrees.get("http://www.ics.uci.edu/~lopes/"));
		
		// start iteration
		int count = 5;
		float d = (float) 0.85;
		
		for (int i = 0; i < count; i++) {		
			for (String key: pageRanks.keySet()) {
				
				float newPageRank = 1 - d;
				
				for (String s: links.get(key)) {
					if (pageRanks.containsKey(s)) {
						newPageRank += pageRanks.get(s) / outDegrees.get(s);
					}
				}
				
				pageRanks.put(key, newPageRank);
			}
		}
		return pageRanks;
	}
	
	public static void writeToLog(Map<String, Float> pageRank, String filePath) {
		try {
			FileWriter fw = new FileWriter(filePath);
			for (Map.Entry<String, Float> entry: pageRank.entrySet()) {
				fw.write(entry.getKey() + " " + entry.getValue().toString() + '\n');
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map<String, Float> loadPageRank(String filePath) {
		Map<String, Float> pageRank = new HashMap<String, Float>();
		
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			while (line != null) {
				String[] subStrs = line.split(" +");
				pageRank.put(subStrs[0], Float.parseFloat(subStrs[1]));
				line = br.readLine();
			}
			
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pageRank;
	}
	
	public static void main(String args[]) throws Exception {
		//float[] input = {0, 0, 0, 0, 0};
		//System.out.println("DCG@5: " + DCG(input, 7));
		//System.out.println("NDCG@5: " + NDCG(input, 5));
		
		Map<String, Float> pageRank = computePageRanks("C:\\Users\\Leopold\\Downloads\\webdata\\link.data");
		writeToLog(pageRank, "C:\\Users\\Leopold\\Downloads\\webdata\\pageRank.data");
		Map<String, Float> loadedPageRank = loadPageRank("C:\\Users\\Leopold\\Downloads\\webdata\\pageRank.data");
		System.out.println("http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg18&position=chr2%3A30370009-30370019"
							+ ", value: "
							+ loadedPageRank.get("http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg18&position=chr2%3A30370009-30370019"));
		
		System.out.println("executed.");
	}
}
