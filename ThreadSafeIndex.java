import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Keeps a mapping of words to the files they were found in and all of the
 * positions in those files.
 */
public class ThreadSafeIndex extends InvertedIndex {

	private final ReadWriteLock lock;

	/**
	 * Initializes the inverted index.
	 */
	public ThreadSafeIndex() {
		super();
		this.lock = new ReadWriteLock();
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
	@Override
	public void add(String word, String fileName, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, fileName, position);
		} finally {
			lock.unlockReadWrite();
		}
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
	@Override
	public void addAll(String[] words, String htmlFile) {
		lock.lockReadWrite();
		try {
			super.addAll(words, htmlFile);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void addAll(InvertedIndex other) {
		lock.lockReadWrite();
		try {
			super.addAll(other);
		} finally {
			lock.unlockReadWrite();
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
	@Override
	public boolean containsWord(String word) {
		lock.lockReadOnly();
		try {
			return super.containsWord(word);
		} finally {
			lock.unlockReadOnly();
		}
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
	@Override
	public boolean containsFile(String word, String fileName) {
		lock.lockReadOnly();
		try {
			return super.containsFile(word, fileName);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the number of keys in the index.
	 *
	 * @return number of keys in the index
	 */
	@Override
	public int size() {
		lock.lockReadOnly();
		try {
			return super.size();
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the number of keys in the TreeMap value of the key word.
	 *
	 * @param word
	 *            key of the TreeMap we want to know the size of
	 *
	 * @return number of file names in the TreeMap of the key word
	 */
	@Override
	public int numFiles(String word) {
		lock.lockReadOnly();
		try {
			return super.numFiles(word);
		} finally {
			lock.unlockReadOnly();
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
	@Override
	public int numPositions(String word, String fileName) {
		lock.lockReadOnly();
		try {
			return super.numPositions(word, fileName);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Passes in a path to an HTML file and strips the file of all the HTML.
	 *
	 * @param output
	 *            path to the file where the index will be written
	 */
	@Override
	public void toJSON(Path output) {
		lock.lockReadOnly();
		try {
			super.toJSON(output);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Takes in parsed queries and returns a list of results that match the
	 * queries exactly.
	 *
	 * @param queries
	 *            parsed queries
	 * @return list of results
	 */
	@Override
	public ArrayList<Result> exactSearch(String[] queries) {

		lock.lockReadOnly();
		try {
			return super.exactSearch(queries);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Takes in parsed queries and returns a list of results that start with the
	 * queries.
	 *
	 * @param queries
	 *            parsed queries
	 * @return list of results
	 */
	@Override
	public ArrayList<Result> partialSearch(String[] queries) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(queries);
		} finally {
			lock.unlockReadOnly();
		}

	}

}