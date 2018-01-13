import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Builds a mapping of words to the file and position in that file it was found,
 * given a path to a directory or file. Parses a query file, generates a sorted
 * list of results from the index, and writes the results to a JSON file.
 * 
 * @author Kelsea Flores
 */
public class Driver {

	/**
	 * Parses command line arguments.
	 * 
	 * @param args
	 *            command line args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException {

		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = null;
		ThreadSafeIndex threadedIndex = null;
		WorkQueue queue = null;
		QueryProcessorInterface qp = null;
		WebCrawler crawler = null;
		int numThreads;

		if (argMap.hasFlag("-threads") || argMap.hasFlag("-url")) {
			threadedIndex = new ThreadSafeIndex();
			index = threadedIndex;
			numThreads = argMap.getInteger("-threads", 5);
			if (numThreads <= 0) {
				numThreads = 3;
			}
			queue = new WorkQueue(numThreads);
			qp = new MultiThreadedQueryProcessor(threadedIndex, queue);
			crawler = new WebCrawler(queue, threadedIndex);
			if (argMap.hasFlag("-limit")) {
				int limit = argMap.getInteger("-limit", 50);
				crawler.crawl(new URL(argMap.getString("-url")), limit);
				queue.finish();
			}
		} else {
			index = new InvertedIndex();
			qp = new QueryProcessor(index);
		}

		if (argMap.hasFlag("-path")) {
			String pathVal = argMap.getValue("-path");
			Path dirPath = null;
			if ((pathVal == null) || !Files.exists(Paths.get(pathVal).normalize())) {
				System.out.println("ERROR: Directory path passed in does not exist.");
				return;
			} else if (Files.exists(Paths.get(pathVal))) {
				dirPath = Paths.get(pathVal).normalize();
			}

			if (threadedIndex == null) {
				IndexBuilder.processFileList(dirPath, index);
			} else {
				ThreadSafeIndexBuilder.processFileList(dirPath, threadedIndex, queue);
			}
		}

		if (argMap.hasFlag("-port")) {
			Server server = new Server(argMap.getInteger("-port", 8080));
			ServletHandler handler = new ServletHandler();
			handler.addServletWithMapping(new ServletHolder(new SearchServlet(index)), "/welcome");

			server.setHandler(handler);
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				server.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (argMap.hasFlag("-index")) {
			Path indexFile = Paths.get(argMap.getString("-index", "index.json"));
			index.toJSON(indexFile);
		}

		if (argMap.hasFlag("-query")) {
			if (argMap.getString("-query") != null) {
				Path queryFile = Paths.get(argMap.getValue("-query"));
				qp.processQueries(argMap.hasFlag("-exact"), queryFile);

			}
		}

		if (argMap.hasFlag("-results")) {
			Path indexFile = Paths.get(argMap.getString("-results", "results.json"));
			qp.toJSON(indexFile);
		}

		if (queue != null) {
			queue.shutdown();
		}
	}
}