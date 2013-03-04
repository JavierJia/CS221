package ranker;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Hello world!
 * 
 */
public class App {
	/**
	 * Index all text files under a directory.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		String usage = "java -jar index|search"
				+ " [-index INDEX_PATH] [-docs DOCS_PATH] [-queryfile queryfiles] [-pagerank PAGERANK_FILE]"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
				+ "in INDEX_PATH that can be searched with SearchFiles";
		String indexPath = "index";
		String docsPath = null;
		String pageRankPath = null;
		String queryFilePath = null;
		boolean bIndex = false;
		boolean bSearch = false;
		if (args.length < 2) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}
		if ("index".equalsIgnoreCase(args[0])) {
			bIndex = true;
		} else if ("search".equalsIgnoreCase(args[0])) {
			bSearch = true;
		} else {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		for (int i = 1; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-docs".equals(args[i])) {
				docsPath = args[i + 1];
				i++;
			} else if ("-pagerank".equalsIgnoreCase(args[i])) {
				pageRankPath = args[i + 1];
				i++;
			} else if ("-queryfile".equalsIgnoreCase(args[i])) {
				queryFilePath = args[i + 1];
			}

		}

		if (bIndex) {
			if (docsPath == null) {
				System.err.println("Usage: " + usage);
				System.exit(1);
			}

			final File docDir = new File(docsPath);
			if (!docDir.exists() || !docDir.canRead()) {
				System.out
						.println("Document directory '"
								+ docDir.getAbsolutePath()
								+ "' does not exist or is not readable, please check the path");
				System.exit(1);
			}

			Date start = new Date();
			try {
				System.out.println("Indexing to directory '" + indexPath
						+ "'...");

				Indexer indexer = new Indexer(docsPath, indexPath);
				Map<String, Float> pageRank = null;
				if (pageRankPath != null) {
					pageRank = Utility.loadPageRank(pageRankPath);
				}
				indexer.startIndex(pageRank);
				Date end = new Date();
				System.out.println(end.getTime() - start.getTime()
						+ " total milliseconds");

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(" caught a " + e.getClass()
						+ "\n with message: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		if (bSearch) {
			Map<String, Float> pageRank = null;
			if (pageRankPath != null) {
				pageRank = Utility.loadPageRank(pageRankPath);
			}
			Searcher searcher = new Searcher(indexPath,pageRank);
			if (queryFilePath == null) {
				searcher.interactiveSearching();
			} else {
				final File queryFile = new File(queryFilePath);
				if (!queryFile.exists() || !queryFile.canRead()) {
					System.out
							.println("Document directory '"
									+ queryFile.getAbsolutePath()
									+ "' does not exist or is not readable, please check the path");
					System.exit(1);
				}
				searcher.batchSearching(queryFile);
			}
		}
	}
}
