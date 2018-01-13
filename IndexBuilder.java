import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Builds the index that store a mapping from a word the file(s) and position(s)
 * in that file it was found in.
 * 
 * @author Kelsea Flores
 */
public class IndexBuilder {

	/**
	 * Passes in the path to either a file or directory and an ArrayList and
	 * adds all HTML files (files ending in ".html" or ".htm") to the class
	 * member fileList.
	 *
	 * @param dir
	 *            path to a directory to traverse or to a file
	 *
	 * @param fileList
	 *            an ArrayList where all HTML files will be stored
	 */
	public static void findHTMLFiles(Path dir, ArrayList<Path> fileList) {

		if (Files.isDirectory(dir)) {
			try (DirectoryStream<Path> paths = Files.newDirectoryStream(dir)) {
				for (Path path : paths) {
					findHTMLFiles(path, fileList);
				}
			} catch (IOException e) {
				System.out.println("Invalid directory path passed in. Exiting program.");
			}
		} else {
			String strPath = dir.toString().toLowerCase();
			if ((strPath.endsWith(".html") || strPath.endsWith(".htm")) && Files.exists(dir.normalize())) {
				fileList.add(dir.normalize());
			}
		}
	}

	/**
	 * Finds all of the HTML files in a given directory.
	 *
	 * @param dir
	 *            path to a directory or single file
	 *
	 * @return a list of all of the HTML files found in the directory
	 */
	public static ArrayList<Path> findHTMLFiles(Path dir) {
		ArrayList<Path> fileList = new ArrayList<>();
		findHTMLFiles(dir, fileList);
		return fileList;
	}

	/**
	 * Passes in a path to an HTML file and strips the file of all the HTML.
	 *
	 * @param htmlFile
	 *            path to the HTML file to be cleaned
	 *
	 * @return the cleaned HTML file (the file after being stripped of all the
	 *         HTML)
	 */
	public static String cleanHTMLFile(Path htmlFile) {
		String cleanedHTML = "";
		try {
			String html = new String(Files.readAllBytes(htmlFile), Charset.forName("UTF-8"));
			cleanedHTML = HTMLCleaner.stripHTML(html);

		} catch (IOException e) {
			System.out.println("ERROR: Given invalid path when cleaning HTML.");
		}
		return cleanedHTML;
	}

	/**
	 * Passes in the path to an HTML file and strips it of all the HTML. Calls
	 * cleanHTMLFile on that file to return a clean (HTML-free) version of the
	 * file. Parses the String into words and adds each word accordingly to the
	 * index.
	 *
	 * @param htmlFile
	 *            path to the HTML file
	 * 
	 * @param index
	 *            index to add the words, file names, and positions to
	 *
	 */
	public static void parseHTMLFile(Path htmlFile, InvertedIndex index) {

		String fileName = htmlFile.normalize().toString();
		String cleanedHTML = cleanHTMLFile(htmlFile).toLowerCase();
		String[] words = WordParser.parseWords(cleanedHTML);

		index.addAll(words, fileName);

	}

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
	public static void processFileList(Path dir, InvertedIndex index) {

		ArrayList<Path> paths = findHTMLFiles(dir);

		for (Path path : paths) {
			parseHTMLFile(path, index);
		}
	}

}