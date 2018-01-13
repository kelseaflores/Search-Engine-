import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	private static final String TITLE = "Search Engine";

	InvertedIndex index;
	private String results;

	public SearchServlet(InvertedIndex index) {
		super();
		this.index = index;
		results = "";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body>%n");

		out.printf("<h1>Search Engine</h1>%n%n");
		printForm(request, response);

		long totalTime = 0;

		ArrayList<Result> searchResults = null;
		if (results != null) {
			long startTime = System.currentTimeMillis();
			String[] parsedQuery = WordParser.parseWords(results);
			searchResults = index.partialSearch(parsedQuery);
			long endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			for (Result r : searchResults) {
				out.printf("<p>");
				out.printf("<a href=\"%s\"> %s </a>", r.path(), r.path());
				out.printf("</p>");
			}
		}

		out.printf("<p>Total number of results: %s</p>", searchResults.size());
		out.printf("<p>This search took %s milliseconds</p>", totalTime);

		out.printf("%n</body>%n");
		out.printf("</html>%n");

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		String query = request.getParameter("query");
		query = query == null ? "" : query;
		results = query;

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<div>");
		out.printf("<center><label for=\"search\">Search:</label>");
		out.printf("<input type=\"text\" name=\"query\" maxlength=\"100\" size=\"60\">%n");
		out.printf("</div>");
		out.printf("<p><div class=\"button\">");
		out.printf("<button type=\"submit\">Submit</button>");
		out.printf("</center></div></p>");
		out.printf("</form>\n%n");
	}
}