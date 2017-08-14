package AccountableDec.JavaServer;

import static Database.Database.adminSearch;
import static Database.Database.getConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.json.simple.JSONObject;

import com.google.protobuf.ByteString;

import Database.Database;
import Database.DecryptionRequest;
import Database.Proofs;
import DeviceRPC.DeviceClient;
import io.grpc.decryptiondevice.Record;
import io.grpc.decryptiondevice.RootTreeHash;
import org.apache.commons.codec.binary.Hex;
public class Servlet extends HttpServlet {
	Connection conn;
	DeviceClient rpcClient;
	Set<String[]> basket = new HashSet<String[]>();
	Map<String,HashSet<DecryptionRequest>>decryptedFiles = new HashMap<String,HashSet<DecryptionRequest>>();

	public void init() throws ServletException{
		try {
			conn = Database.getConnection();
			rpcClient = new DeviceClient("localhost", 50051);
		} catch (SQLException e) {
			System.out.println("couldn't connect to  DB");
			e.printStackTrace();
		}
	}

	public void destroy(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handles all requests to the server, hanldes accordingly
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException{
		System.out.println(request.getServletPath());
		response.setStatus(HttpStatus.OK_200);
		Scanner scanner = null;
		if(request.getServletPath().equals("/") ){
			scanner = new Scanner(new File("index.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if(request.getServletPath().equals("/searchPage")){
			scanner = new Scanner(new File("adminSearch.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if(request.getServletPath().equals("/basket")){
			response.getWriter().println( viewBasket(request));
		}else if(request.getServletPath().equals("/userInspection")){
			scanner = new Scanner(new File("userInspection.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if (request.getServletPath().equals("/proofsInfo")){
			scanner = new Scanner(new File("proofsInfo.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if (request.getServletPath().equals("/proofsPresence")){
			scanner = new Scanner(new File("proofsPresence.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if (request.getServletPath().equals("/proofsExtension")){
			scanner = new Scanner(new File("proofsExtension.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if (request.getServletPath().equals("/proofsAbsence")){
			scanner = new Scanner(new File("proofsAbsence.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if (request.getServletPath().equals("/decryptedFiles")){
			scanner = new Scanner(new File("decryptedFiles.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}else if (request.getServletPath().equals("/proofsDevice")){
			scanner = new Scanner(new File("proofsDevice.html"));
			response.getWriter().println(scanner.useDelimiter("\\A").next());
		}

	}

	/**
	 * post requests where data is sent to the server e.g. searches/requests ect.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.OK_200);
		Scanner scanner = null;
		try {
			if(request.getServletPath().equals("/search")){
				response.getWriter().println(getSearchResults(request));
			}else if(request.getServletPath().equals("/addToBasket")){
				System.out.println("request made");
				response.getWriter().println(addToBasket(request));
			}else if(request.getServletPath().equals("/del")){
				delFromBasket(request);
				response.getWriter().println(viewBasket(request));
			}else if(request.getServletPath().equals("/requestDec")){
				requestDecryptions(request);
				scanner = new Scanner(new File("adminSearch.html"));
				response.getWriter().println(scanner.useDelimiter("\\A").next());
			}else if(request.getServletPath().equals("/viewRequests")){
				response.getWriter().println(viewDecryptions(request));
			}else if (request.getServletPath().equals("/getProofPresence")){
				response.getWriter().println(getProofPresence(request));
			}else if (request.getServletPath().equals("/getProofExtension")){
				response.getWriter().println(getProofExtension(request));
			}else if (request.getServletPath().equals("/getProofAbsence")){
				response.getWriter().println(getProofAbsence(request));
			}else if (request.getServletPath().equals("/getPlainTexts")){
				response.getWriter().println(getPlainTexts(request));
			}else if (request.getServletPath().equals("/getDeviceInfo")){
				response.getWriter().println(getDeviceInfo(request));
			}else if (request.getServletPath().equals("/downloadPlainText")){
				response.setContentType("text/plain");
				response.setHeader("Content-Disposition", "attachment;filename=" + request.getParameter("hash"));
				response.getOutputStream().write(getPlainText(request));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDeviceInfo(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;
		//		generateProofFile(r);
		Scanner scanner = new Scanner(new File("proofsDevice.html"));

		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.contains("<!-- device -->")){

				html = html.concat("		<h1 class=\"title\">Current Root Tree Hash</h2>\r\n<p>" + 
						Hex.encodeHexString(rpcClient.getRootTreeHash(ByteString.copyFromUtf8("nonce")).getRth().toByteArray()) + 
						"				</p><br><br>\r\n" + 
						"				<h2 class=\"title\">Signature on RTH</h2>\r\n<p>" + 
						Hex.encodeHexString(rpcClient.getRootTreeHash(ByteString.copyFromUtf8("nonce")).getSig().toByteArray()) + 
						"				</p><br><br>\r\n" + 
						"				<h2 class=\"title\">Public Encryption Key</h2>\r\n" + 
						rpcClient.getPublicKey(ByteString.copyFromUtf8("nonce")).getRSAEncryptionKey().toStringUtf8() + 
						"				<br><br>\r\n" + 
						"				<h2 class=\"title\">Verification Key</h2>\r\n" + 
						rpcClient.getPublicKey(ByteString.copyFromUtf8("nonce")).getRSAVerificationKey().toStringUtf8() + 
						"				<br>");

			}
		}
		return html;
	}

	public byte[] getPlainText(HttpServletRequest r){
		String name= r.getParameter("name");
		String hash = r.getParameter("hash");
		HashSet<DecryptionRequest> reqs = decryptedFiles.get(name);
		for(DecryptionRequest req:reqs){
			if(req.getFileHash().equals(hash)){
				if(req.getDecryptedFile()!= null){
					return req.getDecryptedFile();
				}else{
					System.err.println("File not decrypted propperly - decrpytedFile = null");
				}
			}
		}
		return null;

	}


	public String getPlainTexts(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;
		String user = r.getParameter("name");
		//		generateProofFile(r);
		Scanner scanner = new Scanner(new File("decryptedFiles.html"));

		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.contains("<!--table-->")){
				try{
					html = html.concat(generatePlainTextTable(r));
				}catch (SQLException e) {
					html="";
					e.printStackTrace();
				}
			}if(line.contains("<!-- var -->")){
				html = html.concat("<input type=\"hidden\" name=\"name\" value = \""+user+"\">");
			}
		}
		return html;
	}

	private String generatePlainTextTable(HttpServletRequest r) throws SQLException{
		String name = r.getParameter("name");
		HashSet<DecryptionRequest> requests = decryptedFiles.get(name);
		String html = "";int i = 0;
		for(DecryptionRequest row:requests){
			html=html.concat("					<tr>\r\n" + 
					"						<td>"+row.getOwner()+"</td>\r\n" + 
					"						<td>"+row.getTimestampFile()+"</td>\r\n" + 
					"						<td>"+row.getReason()+"</td>\r\n" + 
					"						<td>"+row.getFileHash()+"</td>\r\n" + 
					"						<td><button class=\"button is-primary\" name =\"hash\" value="+row.getFileHash()+">Download</td>\r\n "+
					"					</tr>	");
		}
		return html;
	}

	public String getProofAbsence(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;
		String user = r.getParameter("name");
		try{
			String proof = Proofs.proveAbsence(conn,r.getParameter("name")).toJSONString();
			//		generateProofFile(r);
			Scanner scanner = new Scanner(new File("proofsAbsence.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-primary\" type=\"text\" >"+proof+"</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NullPointerException ex){
			Scanner scanner = new Scanner(new File("proofsAbsence.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-danger\" type=\"text\" >Proof Generation Failed - Username not found</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}
		return html;
	}


	/**
	 * Generates proof of Extesnion from the input and places it in a text area inside proofsExtesnion.html
	 * @param r HttpServletRequest
	 * @return Full html of page to be displayed
	 * @throws FileNotFoundException
	 * */
	public String getProofExtension(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;
		String user = r.getParameter("name");
		try{
			String proof = Proofs.proveExtension(conn, r.getParameter("old"),r.getParameter("new")).toJSONString();
			//		generateProofFile(r);
			Scanner scanner = new Scanner(new File("proofsExtension.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-primary\" type=\"text\" >"+proof+"</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NullPointerException ex){
			Scanner scanner = new Scanner(new File("proofsExtension.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-danger\" type=\"text\" >Proof Generation Failed - NewRTH is not an append only extesnion of OldRTH"
							+ "\nCheck you have put the correct RTH in the correct area.</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}
		return html;
	}

	/**
	 * Generates proof of presence from the input and places it in a text area inside proofsPresecnce.html
	 * @param r HttpServletRequest
	 * @return Full html of page to be displayed
	 * @throws FileNotFoundException
	 */
	public String getProofPresence(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;
		String user = r.getParameter("name");
		try{
			int index = Database.getIndex(conn, r.getParameter("leaf"));
			String proof = Proofs.provePresence(r.getParameter("RTH"),index).toJSONString();
			//		generateProofFile(r);
			Scanner scanner = new Scanner(new File("proofsPresence.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-primary\" type=\"text\" >"+proof+"</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NullPointerException ex){
			Scanner scanner = new Scanner(new File("proofsPresence.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-danger\" type=\"text\" >Proof Generation Failed - Leaf not found in tree with the given RTH. "
							+ "Check your RTH is a current or past hash. If in doubt use the hash"
							+ "provided by the device.</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}catch(IllegalArgumentException err){
			Scanner scanner = new Scanner(new File("proofsPresence.html"));

			while(scanner.hasNextLine()){
				line = scanner.useDelimiter(">").nextLine();
				html = html.concat(line);

				if(line.contains("<!-- proof -->")){
					//				html = html.concat("	<a href=\"someproof.json\" download >Download Proof</a>");
					html = html.concat("<div class=\"field\">\r\n" + 
							"  <div class=\"control\">\r\n" + 
							"    <textarea class=\"textarea is-danger\" type=\"text\" >Proof Generation Failed - Leaf not found in tree with the given RTH. "
							+ "Check your RTH is a current or past hash. If in doubt use the hash"
							+ "provided by the device.</textarea>\r\n" + 
							"  </div>\r\n" + 
							"</div>");
				}
			}
		}
		return html;
	}

	/**
	 * Displays all decryption requests for a given user specified in the request
	 * @param r http request
	 * @return html of userInspection.html with all requests inserted in table form
	 * @throws FileNotFoundException
	 */
	public String viewDecryptions(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;
		String user = r.getParameter("name");
		//		generateProofFile(r);
		Scanner scanner = new Scanner(new File("userInspection.html"));

		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.contains("<!--table-->")){
				try{
					html = html.concat(generateInspectionTable(r));
				}catch (SQLException e) {
					html="";
					e.printStackTrace();
				}
			}
		}
		return html;
	}

	/**
	 * Generates the table with all decryption requests for a given user - used by viewDecryptions
	 * @param r
	 * @return html snippet containing n table rows
	 * @throws SQLException
	 */
	private String generateInspectionTable(HttpServletRequest r) throws SQLException{
		ArrayList<String[]> requests = Proofs.getRequests(conn, r.getParameter("name"));
		String html = "";int i = 0;
		for(String[] row:requests){
			html=html.concat("					<tr>\r\n" + 
					"						<td>"+row[1]+"</td>\r\n" + 
					"						<td>"+row[0]+"</td>\r\n" + 
					"						<td>"+row[4]+"</td>\r\n" + 
					"						<td>"+row[3]+"</td>\r\n" + 
					"						<td>"+row[2]+" </td>\r\n "+
					"					</tr>	");
		}
		return html;
	}

	/**
	 * Triggered when checking out the decryption basket. All requests are added to the log and decrpytions requested to device **
	 * @param r http request
	 */
	public void requestDecryptions(HttpServletRequest r){
		try{
			//			RootTreeHash currentRTH = rpcClient.getRootTreeHash(ByteString.copyFromUtf8("nonce"));
			//			String rth = currentRTH.getRth().toString();
			//TODO use actual rth from device ^^
			String rth = Database.getRoot(conn);
			String name = r.getParameter("name");
			System.out.println("got to here - servelt - reququesst dec");
			HashSet<DecryptionRequest> requests;
			if(!decryptedFiles.containsKey(name)){
			
				requests = new HashSet<DecryptionRequest>();
				decryptedFiles.put(name, requests);
			}
			for(Iterator<String[]> i = basket.iterator();i.hasNext();){
				String[] row = i.next();
				DecryptionRequest req = new DecryptionRequest(conn, r.getParameter("company"), r.getParameter("name"), row[3], row[2]);
				req.addToDatabase();
				String newRTH = Database.getRoot(conn);
				String proofExtesnion = Proofs.proveExtension(conn, rth, newRTH).toJSONString();
				Record response = rpcClient.decryptRecord(req.getProofOfPresence().toJSONString(),proofExtesnion ,ByteString.copyFrom(req.getFile()));
				req.setDecryptedFile(response.getPlaintext().toByteArray());
				decryptedFiles.get(name).add(req);
			}
			basket.clear();
		}catch (SQLException e) {
			System.err.println("DB connection failed");
			e.printStackTrace();

		}
		//		for(DecryptionRequest i:decryptedFiles){
		//			System.out.println("file decrpted");
		//			System.out.println(i.getFileHash());
		//			System.out.println(i.getDecryptedFile().toString());
		//			try {
		//				FileUtils.writeByteArrayToFile(new File("C:\\Users\\Dom\\Desktop\\Location Files\\domfraisePT.txt"), i.getDecryptedFile());
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		}
	}

	/**
	 * Removes a request that was added from the search page from the field variable basket
	 * Triggered by clicking the delete button on basket.html
	 * @param r the http request
	 */
	public void delFromBasket(HttpServletRequest r){
		for(Iterator<String[]> i = basket.iterator();i.hasNext();){
			String[] row  = i.next();
			if(row[3].equals(r.getParameter("del"))){
				i.remove();
			}
		}
	}

	/**
	 * Displays the basket.html page with any items in the basket inserted into the table
	 * @param r http request
	 * @return String the html file in a string to be written to the response writer
	 * @throws FileNotFoundException
	 */
	public String viewBasket(HttpServletRequest r) throws FileNotFoundException{
		String html  = "";
		String line;

		Scanner scanner = new Scanner(new File("basket.html"));

		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.contains("<!--table-->")){
				html = html.concat(generateBasketTable(r));
			}
		}
		return html;
	}

	/**
	 * Inserts each item in the basket into a html string to be inserted into the basket.html file
	 * @param r http request
	 * @return String the html file in a string to be written to the response writer

	 */
	public String generateBasketTable(HttpServletRequest r){
		String html = "";
		for(String[] row:basket){
			html=html.concat("					<tr>\r\n" + 
					"						<td>"+row[0]+"</td>\r\n" + 
					"						<td>"+row[1]+"</td>\r\n" + 
					"						<td>"+row[2]+"</td>\r\n" + 
					"						<td>"+row[3]+"</td>\r\n" + 
					"						<td><button class=\"delete is-large\" name=\"del\" type=\"submit\" value="+row[3]+"></button></td>\r\n" + 
					"					</tr>	");
		}
		return html;
	}

	/**
	 * Adds a requests to the decryption basket from search page
	 * @param r http request
	 * @return String html file adminSearch in a string. 
	 * @throws FileNotFoundException
	 */
	public String addToBasket(HttpServletRequest r) throws FileNotFoundException{
		//		System.out.println(r.getParameter("name0"));

		System.out.println("meh");
		System.out.println(r.getParameter("reason0"));
		Map<String,String[]> params = r.getParameterMap();
		System.out.println("here");
		Set<Map.Entry<String, String[]>> entrySet = params.entrySet();
		System.out.println("there");
		for(Map.Entry<String, String[]> entry: entrySet){
			if(entry.getKey().substring(0,5).equals("added") && entry.getValue()[0].equals("on")){
				String[] item = new String[4];
				String index = entry.getKey().substring(5,6);
				item[0] = params.get("name"+index)[0];
				item[1] = params.get("timestamp"+index)[0];
				item[2] = params.get("reason"+index)[0];
				item[3] = params.get("hash"+index)[0];
				basket.add(item);
			}

		}
		Scanner scanner = new Scanner(new File("adminSearch.html"));
		String html  = "";
		String line;
		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.contains("<body>")){
				html = html.concat("<script type=\"text/javascript\">alert( \"Item(s) added to basket \nGo to Decryption Basket to view\",\"stuff\" );</script>");
			}
		}
		return html;
	}

	/**
	 * Generates search results and diplayes them in a table to be added to basket
	 * @param r http request
	 * @return String adminSearch.html in a string with search results inserted 
	 * @throws IOException
	 */
	public String getSearchResults(HttpServletRequest r) throws IOException{
		String html  = "";
		String line;

		Scanner scanner = new Scanner(new File("adminSearch.html"));

		while(scanner.hasNextLine()){
			line = scanner.useDelimiter(">").nextLine();
			html = html.concat(line);
			if(line.contains("<!--table-->")){
				html = html.concat(generateSearchTable(r));
			}
		}
		return html;
	}

	/**
	 * Generates tables with rows for each result from seach
	 * @param request
	 * @return String html table to be inserted into the htmlfile
	 */
	public String generateSearchTable(HttpServletRequest request){

		try {

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
			String endHtml = "		"
					+ ""
					+ "<br>\r\n" + 
					"		\r\n" + 
					"		<div class=\"columns\">\r\n" + 
					"			<div class=\"column\">\r\n" + 
					"			\r\n" + 
					"			</div>\r\n" + 
					"			<div class=\"column\" align=\"center\">\r\n" + 
					"				<button class=\"button is-primary\" type = \"submit\" name = \"addToBasket\"  >\r\n" + 
					"				addToBasket\r\n" + 
					"				</button>\r\n" + 
					"			</div>\r\n" + 
					"			<div class=\"column\">\r\n" + 
					"			\r\n" + 
					"			</div>\r\n" + 
					"		\r\n" + 
					"		</div></tbody></table>";


			for(int i = 0;i<results.size() && i<100;i++)
			{
				String hiddenVars=
						"			<input type=\"hidden\" name=\"name"+i+"\" value = \""+results.get(i)[0]+"\">" +
								"			<input type=\"hidden\" name=\"timestamp"+i+"\" value = \""+results.get(i)[1]+"\">" +
								"			<input type=\"hidden\" name=\"hash"+i+"\" value = \""+results.get(i)[2]+"\">" ;
				endHtml =endHtml.concat(hiddenVars);
				html = html.concat(
						"<tr>\r\n" + 
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

		}
	}


}
