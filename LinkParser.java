import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {

	// https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a
	// https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html
	// https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL

	public static enum HTTP {
		OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
	};

	/**
	 * Removes the fragment component of a URL (if present), and properly
	 * encodes the query string (if necessary).
	 *
	 * @param url
	 *            url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	public static List<String> fetchLines(URL url, String request) throws UnknownHostException, IOException {
		ArrayList<String> lines = new ArrayList<>();
		int port = url.getPort() < 0 ? 80 : url.getPort();

		try (Socket socket = new Socket(url.getHost(), port);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {
			// writer.println(request);
			// writer.flush();

			String line = null;

			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	/**
	 * Fetches the HTML (without any HTTP headers) for the provided URL. Will
	 * return null if the link does not point to a HTML page.
	 *
	 * @param url
	 *            url to fetch HTML from
	 * @return HTML as a String or null if the link was not HTML
	 * @throws IOException
	 */
	public static String fetchHTML(URL url) {
		if (url.getPath().endsWith("html")) {
			String html = "";
			try {
				html = HTTPFetcher.fetchHTML(url.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return html;
		} else {
			return null;
		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of
	 * the anchor tags in the provided HTML. The links will be converted to
	 * absolute using the base URL and cleaned (removing fragments and encoding
	 * special characters as necessary).
	 *
	 * @param base
	 *            base url used to convert relative links to absolute3
	 * @param html
	 *            raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html) {
		ArrayList<URL> links = new ArrayList<>();
		String regex = "<a[^>]*\\s*href\\s*=\\s*\"\\s*(.*?)\\s*\"\\s*";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		if (html != null) {
			Matcher matcher = pattern.matcher(html);

			while (matcher.find()) {
				try {
					String match = matcher.group(1);
					if (!match.isEmpty() && match.length() > 0) {
						URL absolute = clean(new URL(base, match));
						if (absolute.getProtocol().toLowerCase().startsWith("http") && !absolute.equals(null)) {
							links.add(absolute);
						}
					}

				} catch (MalformedURLException e) {
					System.out.println("Bad URL!");
				}
			}
		}

		return links;
	}
}