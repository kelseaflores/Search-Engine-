import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Builds the index that store a mapping from a word the file(s) and position(s)
 * in that file it was found in.
 * 
 * @author Kelsea Flores
 */
public class ThreadSafeIndexBuilder {

	/**
	 * Builds the index from all of the HTML files found in the directory, or
	 * from a single HTML file.
	 * 
	 * @param dir
	 *            The path to the directory or file to process
	 *
	 * @return a complete InvertedIndex containing all of the words, file names,
	 *         and positions in those files from the path passed in in
	 *         findHTMLFiles().
	 */
	public static void processFileList(Path dir, ThreadSafeIndex threadedIndex, WorkQueue queue) {

		ArrayList<Path> paths = IndexBuilder.findHTMLFiles(dir);

		for (Path path : paths) {
			queue.execute(new IndexTask(path, threadedIndex));
		}
		queue.finish();
	}

	private static class IndexTask implements Runnable {

		Path path;
		ThreadSafeIndex threadedIndex;

		private IndexTask(Path path, ThreadSafeIndex threadedIndex) {
			this.path = path;
			this.threadedIndex = threadedIndex;
		}

		@Override
		public void run() {
			InvertedIndex local = new InvertedIndex();
			IndexBuilder.parseHTMLFile(path, local);
			threadedIndex.addAll(local);
		}

	}

}