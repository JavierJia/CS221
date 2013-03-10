package ranker;

/*
 004     * Licensed to the Apache Software Foundation (ASF) under one or more
 005     * contributor license agreements.  See the NOTICE file distributed with
 006     * this work for additional information regarding copyright ownership.
 007     * The ASF licenses this file to You under the Apache License, Version 2.0
 008     * (the "License"); you may not use this file except in compliance with
 009     * the License.  You may obtain a copy of the License at
 010     *
 011     *     http://www.apache.org/licenses/LICENSE-2.0
 012     *
 013     * Unless required by applicable law or agreed to in writing, software
 014     * distributed under the License is distributed on an "AS IS" BASIS,
 015     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 016     * See the License for the specific language governing permissions and
 017     * limitations under the License.
 018     */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import analysis.NgramAnalyzer;

/** Simple command-line based search demo. */
public class Searcher {

	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	public final Map<String, Float> pageRank;
	public final Map<String, Float> authorityMap;

	String[] fields = { Indexer.CONTENT_FIELD, Indexer.TITLE_FIELD };
	Map<String,Float> boosts = new HashMap<String,Float>();
	

	public Searcher(String indexPath, final Map<String, Float> pageRank, Map<String, Float> authorityMap) throws IOException, ParseException {
		String index = indexPath;

		boosts.put(Indexer.CONTENT_FIELD, Ranker.BOOST_CONTENT);
		boosts.put(Indexer.TITLE_FIELD, Ranker.BOOST_TITLE);
		
		reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		searcher = new IndexSearcher(reader);
		analyzer = new NgramAnalyzer(Indexer.version);
		this.pageRank = pageRank;
		this.authorityMap = authorityMap;
	}

	public void interactiveSearching() throws IOException, ParseException {
		String queries = null;
		int repeat = 0;
		boolean raw = true;
		String queryString = null;
		int hitsPerPage = 10;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				Indexer.version, fields, analyzer, boosts);
//		QueryParser parser = new QueryParser(Indexer.version, fields[0],
//		 analyzer);
		while (true) {
			if (queries == null && queryString == null) { // prompt the user
				System.out.println("Enter query: ");
			}

			String line = queryString != null ? queryString : in.readLine();

			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim();
			if (line.length() == 0) {
				break;
			}

			Query query = parser.parse(line);
			System.out.println("Searching for: " + query.toString());

			if (repeat > 0) { // repeat & time as benchmark
				Date start = new Date();
				for (int i = 0; i < repeat; i++) {
					searcher.search(query, null, 100);
				}
				Date end = new Date();
				System.out.println("Time: " + (end.getTime() - start.getTime())
						+ "ms");
			}

			doPagingSearch(in, searcher, query, pageRank,authorityMap, hitsPerPage, raw,
					queries == null && queryString == null);

			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}

	public static void doPagingSearch(BufferedReader in,
			IndexSearcher searcher, Query query, final Map<String, Float> pageRank,final Map<String, Float> authorityMap,int hitsPerPage, boolean raw,
			boolean interactive) throws IOException {

		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, Ranker.CANDIDATE_FACTOR * hitsPerPage);
		ScoreDoc[] hits = Ranker.rerank(searcher, results, pageRank, authorityMap);

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out
						.println("Only results 1 - " + hits.length + " of "
								+ numTotalHits
								+ " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) { // output raw format
					System.out.println("doc=" + hits[i].doc + " score="
							+ hits[i].score);
					// continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get(Indexer.URL_FIELD);
				if (path != null) {
					System.out.println((i + 1) + ". " + path);
					String title = doc.get(Indexer.TITLE_FIELD);
					if (title != null) {
						System.out.println("   Title: " + title);
					}
				} else {
					System.out.println((i + 1) + ". "
							+ "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out
							.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit)
					break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}

	public void batchSearching(File queryFile) throws IOException,
			ParseException {
		int hitsPerPage = 20;
		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				Indexer.version, fields, analyzer, boosts);
//		QueryParser parser = new QueryParser(Indexer.version, fields[0],
//				 analyzer);
		BufferedReader in = new BufferedReader(new FileReader(queryFile));

		String line;

		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				break;
			}

			Query query = parser.parse(line);
			System.out.println("Query: " + line);
			doPagingSearch(in, searcher, query, pageRank,authorityMap, hitsPerPage, true, false);
		}
	}
}
