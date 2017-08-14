package Database;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



/**
 * Access and modification of the database for the blockchain
 * 
 * Schema:
 * Tree(node,left,right,size_when_created)
 * Log(leaf_value,index)
 * Roots(RTH,size)
 * Requests(id,company,employee,hash,reason,timestamp,)
 * Files(id,owner,file,timestamp,hash)
 * 
 * @author Dom
 *
 */

public class Database {
	/*
	 * This class makes a connection to the database.
	 * @return conn, the connection that to the the database
	 */
	public static Connection getConnection() throws SQLException{
		try { 

			Class.forName("org.postgresql.Driver").newInstance();
		}
		catch(ClassNotFoundException e) {
			System.out.println("Error: unable to load driver class!");
			System.exit(1);
		}
		catch(IllegalAccessException ex) {
			System.out.println("Error: access problem while loading!");
			System.exit(2);
		}
		catch(InstantiationException ex) {
			System.out.println("Error: unable to instantiate driver!");
			System.exit(3);
		} 
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://mod-fund-databases.cs.bham.ac.uk/", "dxf695", "5tfn4636np");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return conn;

	}
	
	

	/**
	 * Calulates log to a given base log(x) to base base
	 * @param x int value to have the log taken of
	 * @param base int base of logarithm
	 * @return double log_base(x)
	 */
	static double log(int x, int base)
	{
		return (Math.log(x) / Math.log(base));
	}

	/**
	 * gets the next power of 2 above or equal to a given number
	 * @param n int input value
	 * @return int The next square number that is >= n
	 */
	public static int getPowOf2(int n) {
		return  (int)Math.pow(2,(int)Math.ceil(log(n,2)));
	}


	/**
	 * Checks if a value is a power of 2
	 * @param i value to be checked
	 * @return true is i is a power of 2, otherwise false
	 */
	public static boolean isPow2(int i){
		return (i & (i - 1)) == 0;
	}

	/**
	 * Retrieves merkel tree node from DB
	 * @param conn Connection the database connection
	 * @param node String The hash stored in node
	 * @return ResultSet set containing all attribues of node with value = node
	 * @throws SQLException
	 */
	public static ResultSet getNode(Connection conn,String node) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM Tree WHERE node = ? ");
		getNode.setString(1, node);
		ResultSet nodeG = getNode.executeQuery();
		return nodeG;
	}

	/**
	 * Gets current root stored in 'roots' table
	 * @param conn DB Connetection
	 * @return String value of current root stored in database
	 * @throws SQLException
	 */
	public static String getRoot(Connection conn) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM roots ORDER BY size DESC");
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			return "";
		}
		node.next();
		String root = node.getString(1);
		return root;
	}
	/**
	 * Gets root stored in 'roots' table when the tree was a given size
	 * @param conn DB Connetection
	 * @param size the size of the tree which the root is wanted for
	 * @return String value of current root stored in database
	 * @throws SQLException
	 */
	public static String getRootSize(Connection conn,int size) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM roots WHERE size = ? ORDER BY size DESC");
		getNode.setInt(1, size);
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			return "";
		}
		node.next();
		String root = node.getString(1);
		return root;
	}

	/**
	 * Gets The size of the tree when a a given node was created
	 * @param conn DB COnnection
	 * @param leaf String Value of a leaf to be queried
	 * @return int the number of leaf entries in the tree when the leaf was created
	 * @throws SQLException
	 */
	public static int getSizeWhenCreated(Connection conn,String leaf) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT size_when_created FROM tree WHERE node = ?");
		getNode.setString(1, leaf);
		ResultSet size = getNode.executeQuery();
		if(!size.isBeforeFirst()){
			throw new IllegalArgumentException("No entry found in database with given value");
		}
		size.next();
		return size.getInt(1);

	}

	/**
	 * Gets the index of a leaf in the log from left most leaf to right most. starts from 0. order from 
	 * @param conn BD connection
	 * @param value String hash value of node to get index of
	 * @return int Index of leaf with value as its hash
	 * @throws SQLException
	 */
	public static int getIndex(Connection conn,String value) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM log WHERE leaf_value = ? ");
		getNode.setString(1, value);
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			throw new IllegalArgumentException("Value not found in log");
		}
		node.next();
		return node.getInt(2);
	}

	
	/**
	 * Get owner of a file
	 * @param conn
	 * @param value String Filehash in files table
	 * @return String username of owner of file
	 * @throws SQLException
	 */
	public static String getOwner(Connection conn,String value) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT owner FROM files WHERE hash LIKE ? ");
		getNode.setString(1, value);
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			throw new IllegalArgumentException("FIle not found in log");
		}
		node.next();
		return node.getString(1);
	}
	
	/**
	 * Gets timestamp on file
	 * @param conn
	 * @param node String node get time stamp of
	 * @return Timestamp Timestamp of when file was created
	 * @throws SQLException
	 */
	public static Timestamp getFileTimestamp(Connection conn,String node) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT timestamp FROM Files WHERE hash = ? ");
		getNode.setString(1, node);
		ResultSet nodeG = getNode.executeQuery();
		if(!nodeG.isBeforeFirst()){
			return null;
		}
		nodeG.next();
		return nodeG.getTimestamp(1);
	}
	/**
	 * Gets all leaf entries in the tree ordered from left most to right most
	 * @param conn Database Connection
	 * @return ArrayList<String> list of hash values for all leaves
	 * @throws SQLException
	 */
	public static ArrayList<String> getLeaves(Connection conn) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM log ORDER BY index ASC");
		ResultSet node = getNode.executeQuery();
		ArrayList<String> result = new ArrayList<String>();
		while(node.next()){
			result.add(node.getString(1));
		}
		return result;
	}

	/**
	 * Gets hash values for each encrypted file for a given user
	 * @param conn DB Connection
	 * @param user String username of person owning files
	 * @return ArrayList<String> list of hash values for all files
	 * @throws SQLException
	 */
	public static ArrayList<String> getMyFiles(Connection conn,String user) throws SQLException{
		PreparedStatement getNode = conn.prepareStatement("SELECT hash FROM Files WHERE owner = ?");
		getNode.setString(1, user);
		ResultSet node = getNode.executeQuery();
		ArrayList<String> result = new ArrayList<String>();
		while(node.next()){
			result.add(node.getString(1));
		}
		return result;
	}

	/**
	 * Gets information about a decryption request for a file 
	 * @param conn Connection with DB
	 * @param hash the hash of encrypted file that has been requested for decryption
	 * @return String[] [company,employee,hash of file,reason,timestamp of request]
	 * @throws SQLException
	 * @throws IllegalArgumentException Thrown if there is no request for this hash
	 */
	public static String[] getRequest(Connection conn, String hash) throws SQLException,IllegalArgumentException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM Requests WHERE hash = ?");
		getNode.setString(1, hash);
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			throw new IllegalArgumentException("No request entry found for this file");
		}
		node.next();
		String [] request = new String[5];
		request[0] = node.getString(2);
		request[1] = node.getString(3);
		request[2] = node.getString(4);
		request[3] = node.getString(5);
		request[4] = node.getString(6);
		return request;
	}

	/**
	 * gets a sha-256 hash of a file
	 * @param filepath path to file to be hashed
	 * @return String hex digest of hash
	 * @throws IOException
	 */
	public static String hashFile(String filepath) throws IOException{
		Path path = Paths.get(filepath);
		byte[] file = Files.readAllBytes(path);
		return TreeOps.hash(file);
	}

	/**
	 * gets a timestamp to be stored in the databse
	 * @return
	 */
	public static Timestamp getTimestamp(){
		Calendar cal = Calendar.getInstance();
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		return timestamp;
	}

	/**
	 * Gets bytes of a file from DB
	 * @param conn
	 * @param hash String filehash of file to be fetched
	 * @return 
	 * @throws SQLException
	 */
	public static byte[] getFileBytes(Connection conn,String hash) throws SQLException{
		PreparedStatement getFile = conn.prepareStatement("SELECT file FROM Files WHERE hash = ?");
		getFile.setString(1, hash);
		ResultSet file = getFile.executeQuery();
		if(!file.isBeforeFirst()){
			throw new IllegalArgumentException("No file found with given hash");
		}
		file.next();
		byte[] fileBytes = file.getBytes(1);
		return fileBytes;	
	}
	
	/**
	 * Insert a test file at a given date
	 * @param conn
	 * @param user String owner of file
	 * @param file byte[] encrypted file bytes
	 * @param timestamp Timestamp When file is said to have been created
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void insertFileAtDate(Connection conn,String user,byte[] file,Timestamp timestamp) throws IOException, SQLException{
		
		PreparedStatement addFile = conn.prepareStatement("INSERT INTO Files (owner,file,timestamp,hash) VALUES(?,?,?,?)");
		addFile.setString(1, user);
		addFile.setBytes(2, file);
		Calendar cal = Calendar.getInstance();
//		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		addFile.setObject(3, timestamp,java.sql.Types.TIMESTAMP);
		addFile.setString(4, TreeOps.hash(file));
		addFile.executeUpdate();
	}

	/**
	 * inserts a decryption request to the Requests table in DB. returns the hash to be inserted into the merkel tree
	 * @param conn
	 * @param company
	 * @param employee
	 * @param reason
	 * @param fileHash
	 * @return
	 * @throws SQLException
	 */
	public static void insertRequest(Connection conn,String company, String employee, String fileHash,String reason, Timestamp timestamp) throws SQLException{
		PreparedStatement insertRequest = conn.prepareStatement("INSERT INTO Requests(company,employee,hash,reason,timestamp) VALUES(?,?,?,?,?)");
		insertRequest.setString(1, company);
		insertRequest.setString(2,employee);
		insertRequest.setString(3, fileHash);
		insertRequest.setString(4, reason);
		insertRequest.setObject(5, timestamp);
		insertRequest.executeUpdate();

		insertEntry(conn, TreeOps.hash(company.concat(employee).concat(reason).concat(fileHash).concat(timestamp.toString())));		



	}

	/**
	 * Inserts an entry into a merkel tree stored in a database. Added to tables Tree, and Log. Updates all nodes to new values. places new RTH in roots table
	 * @param conn Connection Database connection
	 * @param entry String leaf to be inserted
	 * @throws SQLException
	 */
	public static void insertEntry(Connection conn,String entry) throws SQLException{
		//get current size
		PreparedStatement getCurrentSize = conn.prepareStatement("SELECT MAX(size_when_created) FROM Tree");
		ResultSet sizeSet = getCurrentSize.executeQuery();sizeSet.next();
		int currentSize = sizeSet.getInt(1);

		PreparedStatement insertIntoLog = conn.prepareStatement("INSERT INTO Log(leaf_value,index) VALUES(?,?)");
		insertIntoLog.setString(1, entry);insertIntoLog.setInt(2, currentSize);//index 1 less
		insertIntoLog.executeUpdate();

		PreparedStatement getRoot = conn.prepareStatement("SELECT * FROM roots ORDER BY size DESC LIMIT 1");
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM Tree WHERE node = ?");
		PreparedStatement getParent = conn.prepareStatement("SELECT * FROM Tree WHERE right_child = ? or left_child = ? ORDER BY size_when_created DESC");
		PreparedStatement insertIntoTree = conn.prepareStatement("INSERT INTO Tree (node,left_child,right_child,size_when_created,path_length) VALUES(?,?,?,?,?)");
		PreparedStatement insertIntoRoots = conn.prepareStatement("INSERT INTO Roots(RTH,size) VALUES(?,?)");
		PreparedStatement getPathLength = conn.prepareStatement("SELECT path_length FROM Tree WHERE node = ?");


		ResultSet root = getRoot.executeQuery();
		//empty tree
		if(!root.isBeforeFirst()){
			insertIntoRoots.setString(1,entry);
			insertIntoRoots.setInt(2,currentSize+1);
			insertIntoRoots.executeUpdate();

			insertIntoTree.setString(1,entry);
			insertIntoTree.setNull(2, java.sql.Types.VARCHAR);
			insertIntoTree.setNull(3, java.sql.Types.VARCHAR);
			insertIntoTree.setInt(4, currentSize+1);
			insertIntoTree.setInt(5, 0);

			insertIntoTree.executeUpdate();
			return;

		}
		root.next();
		getNode.setString(1, root.getString(1));
		ResultSet node = getNode.executeQuery();//root
		int nodeSize = currentSize;
		//while not past the end
		while(node.isBeforeFirst()){
			node.next();
			//if node symetric
			if(isPow2(nodeSize)){
				//take root as left, value as right, hash together create node
				String oldValue = node.getString(1);
				String newValue = TreeOps.hash(oldValue.concat(entry));
				getParent.setString(1, oldValue);
				getParent.setString(2, oldValue);

				ResultSet parent = getParent.executeQuery();

				insertIntoTree.setString(1,newValue);
				insertIntoTree.setString(2, oldValue);
				insertIntoTree.setString(3, entry);
				insertIntoTree.setInt(4, currentSize+1);

				getPathLength.setString(1, oldValue);
				ResultSet len = getPathLength.executeQuery();
				len.next();
				insertIntoTree.setInt(5, len.getInt(1)+1);

				insertIntoTree.executeUpdate();
				//insert leaf
				insertIntoTree.setString(1,entry);
				insertIntoTree.setNull(2, java.sql.Types.VARCHAR);
				insertIntoTree.setNull(3, java.sql.Types.VARCHAR);
				insertIntoTree.setInt(4, currentSize+1);
				insertIntoTree.setInt(5, 0);

				insertIntoTree.executeUpdate();


				//loop till no parent
				String newHash;
				if(node.getString(2)==null){
					newHash =TreeOps.hash(node.getString(1).concat(entry));

				}else{
					newHash = TreeOps.hash(node.getString(2).concat(node.getString(3)));
				}
				while(parent.isBeforeFirst()){
					parent.next();
					newHash = TreeOps.hash(parent.getString(2).concat(newValue));
					insertIntoTree.setString(1,newHash);
					insertIntoTree.setString(2, parent.getString(2));
					insertIntoTree.setString(3, newValue);
					insertIntoTree.setInt(4, currentSize+1);

					getPathLength.setString(1,parent.getString(2));
					len = getPathLength.executeQuery();
					len.next();
					insertIntoTree.setInt(5, len.getInt(1)+1);


					insertIntoTree.executeUpdate();

					oldValue = parent.getString(1);
					newValue = newHash;
					getParent.setString(1, oldValue);
					getParent.setString(2, oldValue);

					parent = getParent.executeQuery();
				}
				insertIntoRoots.setString(1,newValue);
				insertIntoRoots.setInt(2,currentSize+1);
				insertIntoRoots.executeUpdate();
				break;
				//get parent of old root, update that right node
				//node = parent of old root

			}
			//asymetric
			else{
				getNode.setString(1, node.getString(3));
				node = getNode.executeQuery();

				nodeSize -= getPowOf2(nodeSize)/2;
			}
		}
	}

	/**
	 * Inserts a file to Files table in DB
	 * @param conn
	 * @param user Stringusername of owner of file
	 * @param file byte[] the bytes of the file
	 * @throws SQLException
	 */
	public static void insertFileAsBytes(Connection conn,String user,byte[] file) throws SQLException, IOException{
		PreparedStatement addFile = conn.prepareStatement("INSERT INTO Files (owner,file,timestamp,hash) VALUES(?,?,?,?)");
		addFile.setString(1, user);
		addFile.setBytes(2, file);
		Calendar cal = Calendar.getInstance();
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		addFile.setObject(3, timestamp,java.sql.Types.TIMESTAMP);
		addFile.setString(4, TreeOps.hash(file));
		addFile.executeUpdate();
	}

	/**
	 * Inserts file from filepath
	 * @param conn
	 * @param user String username of owner of file
	 * @param filepath path to file
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void insertFileAsPath(Connection conn,String user,String filepath) throws IOException, SQLException{
		Path path = Paths.get(filepath);
		byte[] file = Files.readAllBytes(path);
		insertFileAsBytes(conn, user, file);
	}


	//create propper time stamp from entries


	/**
	 * Searches for files between a two dates and times
	 * @param conn
	 * @param keyword
	 * @param startDate
	 * @param endDate
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<String[]> adminSearch(Connection conn,String keyword,String startDate, String endDate, String startTime,String endTime) throws SQLException{
		if(keyword.equals("") || startDate.equals("") || endDate.equals("") ||startTime.equals("")||endTime.equals("")){
			return new ArrayList<String[]>();
		}
		LocalDate start = LocalDate.of(Integer.parseInt(startDate.substring(6,10)), Integer.parseInt(startDate.substring(3,5)), Integer.parseInt(startDate.substring(0,2)));
		LocalDate end = LocalDate.of(Integer.parseInt(endDate.substring(6,10)), Integer.parseInt(endDate.substring(3,5)), Integer.parseInt(endDate.substring(0,2)));

		
		LocalTime startT = LocalTime.of(Integer.parseInt(startTime.substring(0,2)), Integer.parseInt(startTime.substring(3,5)));
		LocalTime endT = LocalTime.of(Integer.parseInt(endTime.substring(0,2)), Integer.parseInt(endTime.substring(3,5)));

		PreparedStatement query = conn.prepareStatement("SELECT * FROM files WHERE owner LIKE ? ");
		
		query.setString(1, keyword);

		ResultSet r = query.executeQuery();
		ArrayList<String[]> result = new ArrayList<String[]>();
		while(r.next()){
			String timestamp = r.getTimestamp(4).toString();

			LocalDate date = LocalDate.of(Integer.parseInt(timestamp.substring(0,4)), Integer.parseInt(timestamp.substring(5,7)), Integer.parseInt(timestamp.substring(8,10)));
			LocalTime time = LocalTime.of(Integer.parseInt(timestamp.substring(11, 13)),Integer.parseInt(timestamp.substring(14, 16)));
			if(date.isAfter(start) && date.isBefore(end)){
				String[] line = new String[3];
				line[0] = r.getString(2);
				line[1] = r.getString(4);
				line[2] = r.getString(5);
				result.add(line);
			}else if(date.isEqual(start)){
				if (time.isAfter(startT)){
					String[] line = new String[3];
					line[0] = r.getString(2);
					line[1] = r.getString(4);
					line[2] = r.getString(5);

					result.add(line);
				}	
			} else if(date.isEqual(end)){
				if (time.isBefore(endT)){
					String[] line = new String[3];
					line[0] = r.getString(2);
					line[1] = r.getString(4);
					line[2] = r.getString(5);

					result.add(line);
				}
			}

		}
		return result;

	}



}
