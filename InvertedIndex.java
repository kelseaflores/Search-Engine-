import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Keeps a mapping of words to the files they were found in and all of the
 * positions in those files.
 */
public class InvertedIndex {

	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Initializes the inverted index.
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
	}

	/**
	 * Helper add method
	 *
	 * @param word
	 *            word to add
	 * 
	 * @param fileName
	 *            file name (where word was found) to add
	 * 
	 * @param position
	 *            position of the word in the file name to add
	 *
	 */
	private void addHelper(String word, String fileName, int position) {
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<>());
		}
		if (index.get(word).get(fileName) == null) {
			index.get(word).put(fileName, new TreeSet<>());
		}
		index.get(word).get(fileName).add(position);
	}

	/**
	 * Adds a word, and the file and position in that file that it was found in
	 * to the index.
	 *
	 * @param word
	 *            word to add
	 * 
	 * @param fileName
	 *            file name (where word was found) to add
	 * 
	 * @param position
	 *            position of the word in the file name to add
	 *
	 */
	public void add(String word, String fileName, int position) {
		addHelper(word, fileName, position);
	}

	/**
	 * Adds the list of words to the index.
	 *
	 * @param words
	 *            list of words to add to index
	 * 
	 * @param htmlFile
	 *            file words were found in
	 */
	public void addAll(String[] words, String htmlFile) {
		int position = 1;
		for (String word : words) {
			this.addHelper(word, htmlFile, position);
			position++;
		}
	}

	/**
	 * Adds all data in other index that is not in this index to this index.
	 *
	 * @param other
	 *            other index
	 *
	 */
	public void addAll(InvertedIndex other) {
		for (String word : other.index.keySet()) {
			if (!this.index.containsKey(word)) {
				this.index.put(word, other.index.get(word));
			} else {
				for (String path : other.index.get(word).keySet()) {
					if (!this.index.get(word).containsKey(path)) {
						TreeSet<Integer> positions = other.index.get(word).get(path);
						this.index.get(word).put(path, positions);
					} else {
						TreeSet<Integer> positions = other.index.get(word).get(path);
						// this.index.get(word).get(path).addAll(positions);
						if (positions != null) {
							this.index.get(word).get(path).addAll(positions);
						}

					}
				}
			}
		}
	}

	/**
	 * Checks if the index contains a specific word.
	 *
	 * @param word
	 *            word to check if the index contains
	 *
	 * @return true if the index is found in the index false otherwise
	 */
	public boolean containsWord(String word) {
		return index.containsKey(word);
	}

	/**
	 * Checks if a specified file name is a value to a specified word.
	 *
	 * @param word
	 *            key of the fileName to check
	 * 
	 * @param fileName
	 *            fileName to check if in index
	 *
	 * @return true if fileName is a value of the word false otherwise
	 */
	public boolean containsFile(String word, String fileName) {
		return (index.containsKey(word) && index.get(word).containsKey(fileName));
	}

	/**
	 * Returns the number of keys in the index.
	 *
	 * @return number of keys in the index
	 */
	public int size() {
		return index.size();
	}

	/**
	 * Returns the number of keys in the TreeMap value of the key word.
	 *
	 * @param word
	 *            key of the TreeMap we want to know the size of
	 *
	 * @return number of file names in the TreeMap of the key word
	 */
	public int numFiles(String word) {
		if (index.containsKey(word)) {
			return index.get(word).size();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the number of positions in the TreeSet value from the key
	 * fileName, from the key word.
	 *
	 * @param word
	 *            key of the TreeMap<String, TreeSet<Integer>>
	 * 
	 * @param fileName
	 *            key of the TreeSet<Integer>
	 *
	 * @return number of positions found in the TreeSet
	 */
	public int numPositions(String word, String fileName) {
		if (index.containsKey(word) && index.get(word).containsKey(fileName)) {
			return index.get(word).get(fileName).size();
		} else {
			return 0;
		}
	}

	/**
	 * Passes in a path to an HTML file and strips the file of all the HTML.
	 *
	 * @param output
	 *            path to the file where the index will be written
	 */
	public void toJSON(Path output) {
		JSONWriter.asInvertedIndex(index, output);
	}

	/**
	 * Takes in the query and adds the results to the result map.
	 *
	 * @param word
	 *            query
	 * @param map
	 *            result mapping from file name to result
	 * @param results
	 *            list of results
	 * @return list of results
	 */
	private ArrayList<Result> addResults(String word, TreeMap<String, Result> map, ArrayList<Result> results) {
		for (String file : index.get(word).keySet()) {
			int freq = index.get(word).get(file).size();
			int initial = index.get(word).get(file).first();
			if (!map.containsKey(file)) {
				Result result = new Result(freq, initial, file);
				map.put(file, result);
				results.add(result);
			} else {
				map.get(file).addFrequency(freq);
				map.get(file).updatePosition(initial);
			}
		}
		return results;
	}

	/**
	 * Takes in parsed queries and returns a list of results that match the
	 * queries exactly.
	 *
	 * @param queries
	 *            parsed queries
	 * @return list of results
	 */
	public ArrayList<Result> exactSearch(String[] queries) {
		ArrayList<Result> results = new ArrayList<>();
		TreeMap<String, Result> map = new TreeMap<>();

		for (String query : queries) {
			if (index.containsKey(query)) {
				results = addResults(query, map, results);
			}
		}
		Collections.sort(results);
		return results;

	}

	/**
	 * Takes in parsed queries and returns a list of results that start with the
	 * queries.
	 *
	 * @param queries
	 *            parsed queries
	 * @return list of results
	 */
	public ArrayList<Result> partialSearch(String[] queries) {
		ArrayList<Result> results = new ArrayList<>();
		TreeMap<String, Result> map = new TreeMap<>();

		for (String query : queries) {
			if (!query.isEmpty()) {
				for (String word = index.ceilingKey(query); word != null
						&& word.startsWith(query); word = index.higherKey(word)) {
					results = addResults(word, map, results);
				}
			}
		}
		Collections.sort(results);
		return results;
	}

}