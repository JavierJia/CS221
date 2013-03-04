package ranker;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
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
	
	public static final float ARTHOURITY_FACTOR = 10f; 
	
	public static CustomComparator comparator = new CustomComparator();
	
	public static ScoreDoc[] rerank(IndexSearcher searcher, TopDocs results, Map<String, Float> pageRank) throws IOException {
		ScoreDoc[] hits = results.scoreDocs;
		for(ScoreDoc hit : hits){
			Document doc = searcher.doc(hit.doc);
			String urlstr = doc.get(Indexer.URL_FIELD);
			WebURL url = new WebURL(urlstr);
			
			float pageRankFactor = getPageRank(pageRank,url);
			if (hit.score > 3) { // good enough
				hit.score += getAuthorityScore(url);
				hit.score += LAMDA_ROOT_SCORE * ROOT_SCORE/(url.getDepth()+1);
				hit.score += LAMDA_PAGERANK * pageRankFactor;
			}
		}
		Arrays.sort(hits, comparator);
		return hits;
	}
	
	private static float getAuthorityScore(WebURL url) {
		if (url.getPath().startsWith("http://www.ics.uci.edu/")){
			if (!url.getPath().contains("~")){
				return ARTHOURITY_FACTOR / url.getDepth();
			}
		}
		return 0;
	}

	public static float getPageRank(Map<String, Float> pageRank, WebURL url){
		float rank = 0.01f;
		if (pageRank == null){
			return rank;
		}
		if (pageRank.containsKey(url.getPath())){
			rank = pageRank.get(url.getPath());
		}else if (pageRank.containsKey(url.getFullDomain())){
			rank = pageRank.get(url.getFullDomain()) / url.getDepth();
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
