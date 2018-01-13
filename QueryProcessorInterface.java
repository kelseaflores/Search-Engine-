import java.nio.file.Path;

public interface QueryProcessorInterface {

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
	public void processQueries(boolean exact, Path queryFile);

	/**
	 * Writes the result map out to a JSON file.
	 *
	 * @param resultMap
	 *            result map
	 * @param outputFile
	 *            file to write result map to
	 */
	public void toJSON(Path outputFile);
}