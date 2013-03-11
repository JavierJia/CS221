package ranker;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import util.WebURL;

public class Ranker {
	public static final int CANDIDATE_FACTOR = 100;
	public static final float BOOST_TITLE = 1.5f;
	public static final float BOOST_CONTENT = 1.0f;
	
	public static final float ROOT_SCORE = 3f;
	public static final float LAMDA_ROOT_SCORE = 0.1f;
	public static final float LAMDA_PAGERANK = 0.5f;
	public static final float LAMDA_AUTHORITY = 0.5f;
	
	public static final float ARTHOURITY_FACTOR = 10f; 
	
	public static CustomComparator comparator = new CustomComparator();
	
	public static ScoreDoc[] rerank(IndexSearcher searcher, TopDocs results, Map<String, Float> pageRank, Map<String, Float> authorityMap) throws IOException {
		ScoreDoc[] hits = results.scoreDocs;
		Map<String,Integer> deduplicateSite = new HashMap<String,Integer>();
		for(ScoreDoc hit : hits){
		
			Document doc = searcher.doc(hit.doc);
			String urlstr = doc.get(Indexer.URL_FIELD);
			WebURL url = new WebURL(urlstr);
			
			float duplicateFactor = 1.0f;
			if (! deduplicateSite.containsKey(url.getPernalSite())){
				deduplicateSite.put(url.getPernalSite(),1);
			}else{
				int value = deduplicateSite.get(url.getPernalSite());
				deduplicateSite.put(url.getPernalSite(), value +1);
				duplicateFactor /= (value+1);
			}
			
			if (hit.score > 2) { // good enough
				hit.score += LAMDA_AUTHORITY* getAuthorityScore(authorityMap,url);
				hit.score += LAMDA_ROOT_SCORE * ROOT_SCORE/(url.getDepth()+1);
//				float pageRankFactor = getPageRank(pageRank,url);
//				hit.score += LAMDA_PAGERANK * pageRankFactor;
			}
			hit.score *= duplicateFactor;
		}
		Arrays.sort(hits, comparator);
		return hits;
	}
	
	private static float getAuthorityScore(Map<String, Float> authorityMap, WebURL url) {
		if (authorityMap == null){
			return 0;
		}
		if (authorityMap.containsKey(url.getURL())){
			System.err.println("url:" + url.getURL() + "in pagerank");
				return ARTHOURITY_FACTOR ;
		}
		if (authorityMap.containsKey(url.getFullDomain())){
			//return ARTHOURITY_FACTOR/ url.getDepth();
		}
		return 0;
	}

	public static float getPageRank(Map<String, Float> pageRank, WebURL url){
		float rank = 0.01f;
		if (pageRank == null){
			return rank;
		}
		if (pageRank.containsKey(url.getURL())){
			rank = (pageRank.get(url.getURL()))/60;
			System.err.println("url:" + url.getURL() + "in pagerank");
		}else if (pageRank.containsKey(url.getFullDomain())){
			//rank = pageRank.get(url.getFullDomain()) / url.getDepth();
		}
		return rank;
	}
	
	public static class CustomComparator implements Comparator<ScoreDoc> {
	    public int compare(ScoreDoc o1, ScoreDoc o2) {
	    	if ( o1.score < o2.score){
	    		return 1;
	    	}
	    	if ( o1.score > o2.score){
	    		return -1;
	    	}
	    	return 0;
	    }
	}
}
