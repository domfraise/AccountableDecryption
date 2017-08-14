package AccountableDec.JavaServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MetaData.Response;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import static Database.Database.adminSearch;
import static Database.Database.getConnection;;


public class SearchHandler extends AbstractHandler{

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		System.out.println("target = "+target);
		System.out.println("baserequest = "+baseRequest);
		System.out.println("request = "+request);
		System.out.println("response = "+response);



		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		if(target.equals("/")){
			
		}
		String html  = "";
		String line;

		Scanner scanner = new Scanner(new File("C:\\Users\\Dom\\OneDrive\\Documents\\Bham CS\\Project\\HTMLFiles\\adminSearch.html"));
	
		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.startsWith("<!--table-->")){
				 html = html.concat(generateTable(request));
			}
		}

		response.getWriter().println(html);
	}
	
	/**
	 * Generates tables with rows for each result from seach
	 * @param request
	 * @return
	 */
	public static String generateTable(HttpServletRequest request){
		Connection conn = null;
		try {
			conn = getConnection();
			ArrayList<String[]> results = adminSearch(conn, request.getParameter("name"), request.getParameter("startDate"), request.getParameter("endDate"), request.getParameter("startTime"), request.getParameter("endTime"));
			
			String html = "<table class=\"table\">\r\n" + 
					"  <thead>\r\n" + 
					"    <tr>\r\n" + 
					"      <th>User</th>\r\n" + 
					"      <th>Timestamp</abbr></th>\r\n" + 
					"      <th>Insert Justification for request</abbr></th>\r\n" + 
					"      <th>Add to basket</th>\r\n" + 
					"    </tr>\r\n" + 
					"  </thead>\r\n" + 
					"  <tbody>";
					//rows go here
			String endHtml = "		<br>\r\n" + 
					"		\r\n" + 
					"		<div class=\"columns\">\r\n" + 
					"			<div class=\"column\">\r\n" + 
					"			\r\n" + 
					"			</div>\r\n" + 
					"			<div class=\"column\" align=\"center\">\r\n" + 
					"				<button class=\"button is-primary\" type = \"submit\" value = \"submit\" name = \"addToBasket\"  >\r\n" + 
					"				addToBasket\r\n" + 
					"				</button>\r\n" + 
					"			</div>\r\n" + 
					"			<div class=\"column\">\r\n" + 
					"			\r\n" + 
					"			</div>\r\n" + 
					"		\r\n" + 
					"		</div></tbody>";
		

			for(int i = 0;i<results.size();i++)
			{
				html = html.concat("<input class=\"input\" type=\"hidden\" name = \"name"+i+" value="+results.get(i)[0]+">\" + \r\n" + 
						"<input type=\"hidden\" name = \"timstamp"+i+" value="+results.get(i)[1]+">\" + \r\n" + 
						"<input type=\"hidden\" name = \"hash"+i+" value="+results.get(i)[2]+">\" + \r\n" + 

						""
						+ "<tr>\r\n" + 
						"			<td> "+results.get(i)[0]+"</td>\r\n" + 
						"			<td>"+results.get(i)[1]+"</td>\r\n" + 
						"			<td>\r\n" + 
						"				<div class=\"control\">\r\n" + 
						"				<input class=\"input\" type=\"text\" name = \"reason"+i+"\" placeholder=\"Justification\">\r\n" + 
						"				</div>\r\n" + 
						"			</td>\r\n" + 
						"			<td>\r\n" + 
						"				<div>\r\n" + 
						"					<label class=\"checkbox\">\r\n" + 
						" 				 <input type=\"checkbox\" name=\"added"+i+"\">\r\n" + 
						" 					 Add To Basket\r\n" + 
						"					</label>\r\n" + 
						"				</div>\r\n" + 
						"		\r\n" + 
						"		</tr>");
			}
			
			return html.concat(endHtml);
		} catch (SQLException e) {
			e.printStackTrace();
			return "<b>Database access failed</b>";
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
