package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneTest {
	private static final String EXIT = "--X";

	public static void main(String[] args) throws Exception {
		// Indexing
		Analyzer analyser = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyser);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		Path path = Paths.get("dag/modules/NodeAliasLucene");
		Directory directory = FSDirectory.open(path);
		IndexWriter writer = new IndexWriter(directory, config);

		// Searching
		QueryParser parser = new QueryParser("l_alias",
				analyser);
		IndexReader reader = DirectoryReader.open(writer, true);
		IndexSearcher searcher = new IndexSearcher(reader);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int mode = 0;
		while (true) {
			System.out.println("\"" + EXIT + "\" to exit to top.");
			if (mode == 1) {
				// indexing
				System.out.println("Enter alias:");
				String alias = in.readLine();
				if (alias.equalsIgnoreCase(EXIT)) {
					mode = 0;
					continue;
				}
				System.out.println("Enter identity:");
				String ident = in.readLine();
				if (ident.equalsIgnoreCase(EXIT)) {
					mode = 0;
					continue;
				}

				try {
					// Add the document
					Document doc = new Document();
					doc.add(new StringField("o_alias",
							alias, Store.YES));
					// Don't need to save the lowercase field
					doc.add(new StringField("l_alias",
							alias.toLowerCase(), Store.NO));
					doc.add(new StringField("node",
							ident, Store.YES));
					String uniqID = ident + "-" + alias;
					doc.add(new StringField("uid",
							uniqID, Store.NO));
					Term term = new Term("uid", uniqID);

					// Don't add if it already exists
					writer.updateDocument(term, doc);
					System.out.println(ident + " mapped under '" + alias
							+ "'\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (mode == 2) {
				// Searching
				System.out.println("Enter search query:");
				String strQuery = in.readLine().toLowerCase();
				if (strQuery.equalsIgnoreCase(EXIT)) {
					mode = 0;
					continue;
				}

				// Run the query
				try {
					Query query = parser.parse(strQuery);
					TopDocs result = searcher.search(query, 100);
					System.out.println("Found " + result.totalHits
							+ " matching nodes:");
					for (ScoreDoc scoreDoc : result.scoreDocs) {
						Document d = searcher.doc(scoreDoc.doc);
						System.out.println(d.get("node")
								+ ":\""
								+ d.get("o_alias")
								+ "\"");
					}
					System.out.println();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("(I)ndex or (S)earch?");
				String modeStr = in.readLine();
				if (modeStr.equalsIgnoreCase("i"))
					mode = 1;
				else if (modeStr.equalsIgnoreCase("s")) {
					mode = 2;
					// long start = System.currentTimeMillis();
					// writer.commit();
					// System.err.println(System.currentTimeMillis() - start);

					reader.close();
					reader = DirectoryReader.open(writer, true);
					searcher = new IndexSearcher(reader);
				}
			}
		}
	}
}
