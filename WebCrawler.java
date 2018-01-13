import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Multithreaded web crawler that processes URLs and builds a thread safe index.
 */
public class WebCrawler {

	private HashSet<URL> urlSet;
	private ThreadSafeIndex threadedIndex;
	private WorkQueue queue;
	private int max;

	/**
	 * Initializes a web crawler.
	 */
	public WebCrawler(WorkQueue queue, ThreadSafeIndex threadedIndex) {
		urlSet = new HashSet<>();
		this.threadedIndex = threadedIndex;
		this.queue = queue;
		max = 0;
	}

	/**
	 * Crawls a limited number of URLs, and adds a task to the work queue for
	 * every unique URL found.
	 *
	 * @param seed
	 *            seed URL
	 * 
	 * @param limit
	 *            max number of URLs to crawl
	 * 
	 */
	public void crawl(URL seed, int limit) {
		synchronized (urlSet) {
			max += limit;
			urlSet.add(seed);
		}
		queue.execute(new CrawlTask(seed, threadedIndex, max, urlSet, queue));
	}

	private class CrawlTask implements Runnable {
		URL seed;
		ArrayList<URL> urls;
		ThreadSafeIndex threadedIndex;
		int max;
		HashSet<URL> urlSet;
		WorkQueue queue;

		private CrawlTask(URL seed, ThreadSafeIndex threadedIndex, int max, HashSet<URL> urlSet, WorkQueue queue) {
			this.seed = seed;
			urls = new ArrayList<>();
			this.threadedIndex = threadedIndex;
			this.max = max;
			this.urlSet = urlSet;
			this.queue = queue;
		}

		@Override
		public void run() {
			try {
				String html = HTTPFetcher.fetchHTML(seed.toString());
				if (html != null) {
					synchronized (urlSet) {
						urls = LinkParser.listLinks(seed, html);
						for (URL link : urls) {
							if (urlSet.size() == max) {
								break;
							}
							if (!urlSet.contains(link)) {
								urlSet.add(link);
								queue.execute(new CrawlTask(link, threadedIndex, max, urlSet, queue));
							}
						}
					}
					String cleanedHTML = HTMLCleaner.stripHTML(html);
					String[] words = WordParser.parseWords(cleanedHTML);
					InvertedIndex local = new InvertedIndex();
					local.addAll(words, seed.toString());
					IndexBuilder.processFileList(Paths.get(seed.toString()), local);
					threadedIndex.addAll(local);
				}

			} catch (UnknownHostException e) {
				System.out.println("UnknownHostException in CrawlTask.run()");
			} catch (MalformedURLException e) {
				System.out.println("MalformedURLException in CrawlTask.run()");
			} catch (IOException e) {
				System.out.println("IOException in CrawlTask.run()");
			}

		}

	}
}