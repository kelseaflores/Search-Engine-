import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Takes a file of queries and processes it into a list of arrays of queries.
 */
public class QueryProcessor implements QueryProcessorInterface {

	private final TreeMap<String, ArrayList<Result>> resultMap;
	private final InvertedIndex index;

	/**
	 * Creates a new QueryProcessor instance and initializes the result map.
	 */
	public QueryProcessor(InvertedIndex index) {
		resultMap = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Processes the query file into a list of parsed queries. Uses the list of
	 * queries to populate the result map.
	 *
	 * @param exact
	 *            true if performing exact search, false if performing partial
	 *            search
	 * @param queryFile
	 *            path to query file
	 */
	public void processQueries(boolean exact, Path queryFile) {
		try (BufferedReader reader = Files.newBufferedReader(queryFile, Charset.forName("UTF-8"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] cleanedWords = WordParser.parseWords(line);
				Arrays.sort(cleanedWords);
				String cleanedLine = String.join(" ", cleanedWords);
				if (!cleanedLine.equals("")) {
					if (exact) {
						ArrayList<Result> results = index.exactSearch(cleanedWords);
						resultMap.put(cleanedLine, results);
					} else {
						ArrayList<Result> results = index.partialSearch(cleanedWords);
						resultMap.put(cleanedLine, results);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading from query file.");
		}
	}

	/**
	 * Writes the result map out to a JSON file.
	 *
	 * @param resultMap
	 *            result map
	 * @param outputFile
	 *            file to write result map to
	 */
	public void toJSON(Path outputFile) {
		JSONWriter.asResultsPage(resultMap, outputFile);
	}

}