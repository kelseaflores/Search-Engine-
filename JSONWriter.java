import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Writes data structures in JSON format.
 * 
 * @author Kelsea Flores
 */
public class JSONWriter {

	/**
	 * Returns a String with the specified number of tab characters.
	 *
	 * @param times
	 *            number of tab characters to include
	 * @return tab characters repeated the specified number of times
	 */
	public static String indent(int times) {
		char[] tabs = new char[times];
		Arrays.fill(tabs, '\t');
		return String.valueOf(tabs);
	}

	/**
	 * Returns a quoted version of the provided text.
	 *
	 * @param text
	 *            text to surround in quotes
	 * @return text surrounded by quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * Writes the set of elements as a JSON array at the specified indent level.
	 *
	 * @param writer
	 *            writer to use for output
	 * @param elements
	 *            elements to write as JSON array
	 * @param level
	 *            number of times to indent the array itself
	 * @throws IOException
	 */
	private static void asArray(Writer writer, TreeSet<Integer> elements, int level) throws IOException {
		int size = elements.size();
		int i = 0;
		writer.write("[");
		writer.write("\n");
		for (Integer elem : elements) {
			writer.write(indent(level));
			writer.write(elem.toString());
			if (i == size - 1) {
				writer.write("\n");
			} else {
				writer.write(",");
				writer.write("\n");
			}

			i++;
		}
		writer.write("]");

	}

	/**
	 * Writes the set of elements as a JSON array to the path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			asArray(writer, elements, 1);
		}
	}

	/**
	 * Writes the map of elements as a JSON object to the path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			int size = elements.size();
			int i = 0;
			writer.write("{");
			writer.newLine();
			for (String key : elements.keySet()) {
				writer.write(indent(1));
				writer.write(quote(key) + ": " + elements.get(key));
				if (i == size - 1) {
					writer.newLine();
				} else {
					writer.write(",");
					writer.newLine();
				}
				i++;
			}
			writer.write("}");
		}
	}

	/**
	 * Writes the set of elements as a JSON object with a nested array to the
	 * path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object with a nested array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, BufferedWriter writer, int level)
			throws IOException {
		int i = 0;
		writer.write("{");
		writer.newLine();
		for (String key : elements.keySet()) {
			int numKeys = elements.keySet().size();
			writer.write(indent(1) + quote(key) + ": [");
			writer.newLine();
			asArray(writer, elements.get(key), 2);
			writer.write(indent(1));
			writer.write("]");
			if (i == numKeys - 1) {
				writer.newLine();
			} else {
				writer.write(",");
				writer.newLine();
			}
			i++;
		}
		writer.write("}");
	}

	/**
	 * Writes the set of elements as a JSON object with a nested object with a
	 * set of elements with a nested array to the path using UTF8.
	 *
	 * @param index
	 *            elements to write as a JSON object with a nested object with a
	 *            set of elements and a nested array
	 * @param path
	 *            path to write file
	 */
	public static void asInvertedIndex(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Path path) {

		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {

			writer.write("{");
			writer.newLine();
			int i = 0;
			int j = 0;
			int k = 0;
			for (String word : index.keySet()) {
				j = 0;
				int numWords = index.keySet().size();
				writer.write(indent(1) + quote(word) + ": {");
				writer.newLine();
				for (String file : index.get(word).keySet()) {
					k = 0;
					int numFiles = index.get(word).keySet().size();
					writer.write(indent(2) + quote(file) + ": [");
					writer.newLine();
					for (Integer pos : index.get(word).get(file)) {
						int numPos = index.get(word).get(file).size();
						writer.write(indent(3) + pos.toString());
						if (k == numPos - 1 || numPos == 1) {
							writer.newLine();
						} else {
							writer.write(",");
							writer.newLine();
						}
						k++;
					}
					writer.write(indent(2) + "]");
					if (j == numFiles - 1 || numFiles == 1) {
						writer.newLine();
					} else {
						writer.write(",");
						writer.newLine();
					}
					j++;
				}
				writer.write(indent(1) + "}");
				if (i == numWords - 1 || numWords == 1) {
					writer.newLine();
				} else {
					writer.write(",");
					writer.newLine();
				}
				i++;
			}
			writer.write("}");
		} catch (IOException e) {
			System.out.println("ERROR: Invalid path to print index.");
		}
	}

	/**
	 * Writes the map of queries --> list of results as in JSON format to the
	 * path using UTF8.
	 *
	 * @param results
	 *            the map that maps queries to a list of results
	 * @param path
	 *            path to write file
	 */
	public static void asResultsPage(TreeMap<String, ArrayList<Result>> results, Path path) {
		try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
			writer.write("[");
			writer.newLine();
			int i = 0;
			int j = 0;
			for (String query : results.keySet()) {
				int numQueries = results.keySet().size();
				j = 0;
				writer.write(indent(1) + "{");
				writer.newLine();
				writer.write(indent(2));
				writer.write(quote("queries") + ": " + quote(query) + ",");
				writer.newLine();
				writer.write(indent(2) + quote("results") + ": [");
				writer.newLine();
				for (Result r : results.get(query)) {
					int numResults = results.get(query).size();
					writer.write(indent(3) + "{");
					writer.newLine();
					writer.write(indent(4) + quote("where") + ": " + quote(r.path()) + ",");
					writer.newLine();
					writer.write(indent(4) + quote("count") + ": " + r.frequency() + ",");
					writer.newLine();
					writer.write(indent(4) + quote("index") + ": " + r.initialPos());
					writer.newLine();
					writer.write(indent(3) + "}");
					if (j != numResults - 1) {
						writer.write(",");
					}
					writer.newLine();
					j++;
				}
				writer.write(indent(2) + "]");
				writer.newLine();
				writer.write(indent(1) + "}");
				if (i != numQueries - 1) {
					writer.write(",");
				}
				writer.newLine();
				i++;
			}
			writer.write("]");
		} catch (IOException e) {
			System.out.println("ERROR: Invalid path to print results.");
		}
	}
}