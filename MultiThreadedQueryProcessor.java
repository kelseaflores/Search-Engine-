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
public class MultiThreadedQueryProcessor implements QueryProcessorInterface {

	public final TreeMap<String, ArrayList<Result>> resultMap;
	private final ThreadSafeIndex threadedIndex;
	private final ReadWriteLock lock;
	private final WorkQueue queue;

	/**
	 * Creates a new QueryProcessor instance and initializes the result map.
	 */
	public MultiThreadedQueryProcessor(ThreadSafeIndex threadedIndex, WorkQueue queue) {
		resultMap = new TreeMap<>();
		this.threadedIndex = threadedIndex;
		lock = new ReadWriteLock();
		this.queue = queue;
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
				queue.execute(new QueryTask(exact, line));
			}
			queue.finish();
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
		lock.lockReadOnly();
		try {
			JSONWriter.asResultsPage(resultMap, outputFile);
		} finally {
			lock.unlockReadOnly();
		}
	}

	private class QueryTask implements Runnable {

		boolean exact;
		String line;
		ArrayList<Result> results;

		private QueryTask(boolean exact, String line) {
			this.exact = exact;
			this.line = line;
			this.results = new ArrayList<>();
		}

		@Override
		public void run() {
			String[] cleanedWords = WordParser.parseWords(line);
			String cleanedLine;

			if (cleanedWords.length != 0) {
				Arrays.sort(cleanedWords);
				cleanedLine = String.join(" ", cleanedWords);
				if (exact == true) {
					results = threadedIndex.exactSearch(cleanedWords);
				} else {
					results = threadedIndex.partialSearch(cleanedWords);
				}

				lock.lockReadWrite();
				try {
					resultMap.put(cleanedLine, results);
				} finally {
					lock.unlockReadWrite();
				}
			}

		}

	}

}