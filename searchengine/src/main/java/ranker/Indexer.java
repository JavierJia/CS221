package ranker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import util.MD5;
import analysis.NgramAnalyzer;

public class Indexer {
	public static final Version version = Version.LUCENE_41;
	private final String DOCPATH;
	private final String INDEXPATH;
	private IndexWriter writer;
	private HashSet<String> signatureSet;

	public static final String URL_FIELD = "url";
	public static final String CONTENT_FIELD = "content";
	public static final String TITLE_FIELD = "title";
	public static final String PAGERANK_FIELD = "pagerank";

	public Indexer(String docPath, String indexPath) throws IOException {
		if (docPath.endsWith("/")) {
			DOCPATH = docPath.substring(0, docPath.length() - 1);
		} else {
			DOCPATH = docPath;
		}

		INDEXPATH = indexPath;

		Analyzer analyzer = new NgramAnalyzer(Version.LUCENE_41);
		// Analyzer analyzer = new StandardAnalyzer(version);
		IndexWriterConfig iwc = new IndexWriterConfig(version, analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		Directory dir = FSDirectory.open(new File(INDEXPATH));
		writer = new IndexWriter(dir, iwc);
		signatureSet = new HashSet<String>();
	}

	public void startIndex(final Map<String, Float> pageRank)
			throws IOException, NoSuchAlgorithmException {
		indexDocs(new File(DOCPATH), pageRank);
		writer.close();
	}

	public void indexDocs(final File file, final Map<String, Float> pageRank)
			throws IOException, NoSuchAlgorithmException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(new File(file, files[i]), pageRank);
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					return;
				}
				String signature = MD5.GetMD5(fis);
				fis.close();
				if (signatureSet.contains(signature)) {
					return;
				}
				signatureSet.add(signature);
				fis = new FileInputStream(file);

				try {
					Document doc = new Document();

					// convert local path to url
					String localPath = file.getPath();
					String url = localPath.replace(DOCPATH, "http:/");
					if (url.endsWith("index.txt")) {
						url = url.replaceFirst("index.txt", "");
					}

					Field urlField = new TextField(URL_FIELD, url,
							Field.Store.YES);
					// System.out.println("URL: " + url + pageRank.get(url));
					doc.add(urlField);

					if (pageRank != null) {
						float rank = pageRank.get(url);
						FloatField page_rank_field = new FloatField(
								PAGERANK_FIELD, rank, Field.Store.YES);
						doc.add(page_rank_field);
					}

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(fis, "UTF-8"));
					String title = reader.readLine();
					if (title != null && title.length() > 0) {
						TextField titleField = new TextField(TITLE_FIELD,
								title, Field.Store.YES);
						titleField.setBoost(2);
						doc.add(titleField);
					}

					TextField content = new TextField(CONTENT_FIELD, reader);
					doc.add(content);

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						// New index, so we just add the document (no old
						// document can be there):
						System.out.println("adding " + file);
						writer.addDocument(doc);
					} else {
						// Existing index (an old copy of this document may have
						// been indexed) so
						// we use updateDocument instead to replace the old one
						// matching the exact
						// path, if present:
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}

				} finally {
					fis.close();
				}
			}
		}
	}

}
