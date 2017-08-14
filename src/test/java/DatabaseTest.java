

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.PreferenceChangeEvent;

import static Database.Database.*;
import static Database.TreeOps.*;
import static Database.Proofs.*;
import static Database.Database.*;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.GT;

import com.google.protobuf.ByteString;

import Database.Tree;
import DeviceRPC.DeviceClient;
import crypto.Encryption;


public class DatabaseTest {
	private Connection conn;
	@Before
	public void testConnection() {
		try{
			conn = getConnection();
		}catch(SQLException e){
			fail("connection failed");
		}
	}
	//	@Test
	//	public void testGetPow2(){
	//		assertEquals(4,getPowOf2(3));
	//	}
	//	@Test
	//	public void testGetPow21(){
	//		assertEquals(4,getPowOf2(4));
	//	}
	//	@Test
	//	public void testGetPow22(){
	//		assertEquals(8,getPowOf2(6));
	//	}
	//	@Test
	//	public void testGetPow23(){
	//		assertEquals(8,getPowOf2(7));
	//	}
	@Test
	public void testGetPow24(){
		assertEquals(8,getPowOf2(8));
	}
	@Test
	public void testGetlog(){
		for(int i=0;i<10;i++){
			System.out.println("input:"+i+" :"+getPowOf2(i));
		}
	}
	//			@Test
	public void createTables(){

		try {
			//			PreparedStatement createTree = conn.prepareStatement("CREATE TABLE Tree(node varchar(64),left_child varchar(64),right_child varchar(64),size_when_created Integer,path_length Integer)");
			//				PreparedStatement createLog = conn.prepareStatement("CREATE TABLE Log(leaf_value varchar(64),index Integer)");
			//			PreparedStatement createRoots = conn.prepareStatement("CREATE TABLE Roots(RTH varchar(64),size Integer)");
			PreparedStatement createRequests = conn.prepareStatement("CREATE TABLE Requests(id SERIAL,company varchar(64),employee varchar(64),hash varchar(64),reason varchar(200) NOT NULL,timestamp timestamp)");
			//			PreparedStatement createFiles = conn.prepareStatement("CREATE TABLE Files(id SERIAL,owner varchar(64),file bytea,timestamp timestamp,hash varchar(64))");


			//			createFiles.executeUpdate();			
			createRequests.executeUpdate();

			//				createLog.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	//			@Test
	public void dropTables(){
		try {
			PreparedStatement d1 = conn.prepareStatement("DROP TABLE Requests");

			d1.executeUpdate();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//	@Test
	public void clearTable(){
		PreparedStatement del;
		try {
			del = conn.prepareStatement("Delete FROM requests");
			del.executeUpdate();
		}catch(SQLException e){

		}
	}

	//		@Test
	public void showTables(){
		try {
			PreparedStatement createTree = conn.prepareStatement("SELECT n.nspname as \"Schema\",c.relname as \"Name\",CASE c.relkind WHEN 'r' THEN 'table' WHEN 'v' THEN 'view' WHEN 'i' THEN 'index' WHEN 'S' THEN 'sequence' WHEN 's' THEN 'special' END as \"Type\",pg_catalog.pg_get_userbyid(c.relowner) as \"Owner\"FROM pg_catalog.pg_class c    LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace WHERE c.relkind IN ('r','')    AND n.nspname <> 'pg_catalog'    AND n.nspname <> 'information_schema'    AND n.nspname !~ '^pg_toast'AND pg_catalog.pg_table_is_visible(c.oid)ORDER BY 1,2;");
			ResultSet r = createTree.executeQuery();
			while(r.next()){
				System.out.println(r.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//	  @Test
	public void testrRth(){

		try {
			insertEntry(conn, "1");
			insertEntry(conn, "2");
			insertEntry(conn, "3");
			insertEntry(conn, "4");
			insertEntry(conn, "5");
			insertEntry(conn, "6");
			insertEntry(conn, "7");
			insertEntry(conn, "8");
			insertEntry(conn, "9");
			insertEntry(conn, "10");
			insertEntry(conn, "11");
			insertEntry(conn, "12");
			insertEntry(conn, "13");
			insertEntry(conn, "14");
			insertEntry(conn, "15");
			insertEntry(conn, "16");
			insertEntry(conn, "17");
			insertEntry(conn, "18");
			insertEntry(conn, "19");
			insertEntry(conn, "20");
			PreparedStatement roots = conn.prepareStatement("SELECT * FROM roots ORDER BY size ASC");
			ResultSet rootsSet = roots.executeQuery();
			Tree o = new Tree();
			for(int i=1;i<21;i++){
				o = extend(o,Integer.toString(i));
				rootsSet.next();
				assertEquals(o.getValue(), rootsSet.getString(1));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	//	@Test
	public void testInsert1(){
		try {

			insertEntry(conn, "1");
			insertEntry(conn, "2");
			insertEntry(conn, "3");
			insertEntry(conn, "4");
			insertEntry(conn, "5");
			insertEntry(conn, "6");
			insertEntry(conn, "7");
			insertEntry(conn, "8");
			insertEntry(conn, "9");
			insertEntry(conn, "10");
			insertEntry(conn, "11");

			PreparedStatement nodes = conn.prepareStatement("SELECT * FROM Tree ");
			ResultSet nodesSet = nodes.executeQuery();
			String hash12 = hash("12");
			while(nodesSet.next()){
				System.out.println("node: "+nodesSet.getString(1)+" left: "+nodesSet.getString(2)+" right: "+nodesSet.getString(3)+" size: "+nodesSet.getInt(4)+" path length: "+nodesSet.getInt(5));
			}
			PreparedStatement roots = conn.prepareStatement("SELECT * FROM roots ");
			ResultSet rootsSet = roots.executeQuery();
			while(rootsSet.next()){
				System.out.println("root: "+rootsSet.getString(1)+" size: "+rootsSet.getString(2));
			}

			PreparedStatement log = conn.prepareStatement("SELECT * FROM log ");
			ResultSet logSet = log.executeQuery();
			while(logSet.next()){
				System.out.println("entry: "+logSet.getString(1)+" index: "+logSet.getString(2));
			}
			PreparedStatement del;
			del = conn.prepareStatement("Delete FROM Tree");
			del.executeUpdate();
			del = conn.prepareStatement("Delete FROM log");
			del.executeUpdate();
			del = conn.prepareStatement("Delete FROM roots");
			del.executeUpdate();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Initial insersion failed");
		}
	}




	@Test
	public void seeTable(){
		try {
			insertEntry(conn, "1");
			insertEntry(conn, "2");
			insertEntry(conn, "3");
			insertEntry(conn, "4");
			insertEntry(conn, "5");
			insertEntry(conn, "6");
			insertEntry(conn, "7");
			insertEntry(conn, "8");
			insertEntry(conn, "9");
			insertEntry(conn, "10");
			insertEntry(conn, "11");
			PreparedStatement nodes = conn.prepareStatement("SELECT * FROM Tree");
			ResultSet nodesSet = nodes.executeQuery();
			while(nodesSet.next()){
				System.out.println("node: "+nodesSet.getString(1)+" left: "+nodesSet.getString(2)+" right: "+nodesSet.getString(3)+" size: "+nodesSet.getInt(4)+" path length: "+nodesSet.getInt(5));
			}
			PreparedStatement log = conn.prepareStatement("SELECT * FROM log");
			ResultSet logSet = log.executeQuery();
			while(nodesSet.next()){
				System.out.println("log entry: "+logSet.getString(1));
			}
			PreparedStatement hashes = conn.prepareStatement("SELECT * FROM Tree");
			ResultSet hashSet = hashes.executeQuery();
			while(nodesSet.next()){
				System.out.println("root entry: "+hashSet.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Test
	public void insertFiles() throws IOException, SQLException{
		String user = "domfraise";
//		try {
			//			insertFileAsPath(conn, user, "C:\\Users\\Dom\\Documents\\myfile1.txt");
			//			insertFileAsPath(conn, user, "C:\\Users\\Dom\\Documents\\myfile2.txt");
			//			insertFileAsPath(conn, user, "C:\\Users\\Dom\\Documents\\myfile3.txt");
			//			insertFileAsPath(conn, user, "C:\\Users\\Dom\\Documents\\myfile4.txt");
			//			insertFileAsPath(conn, user, "C:\\Users\\Dom\\Documents\\myfile5.txt");
//			String key=Encryption.stripKey("-----BEGIN RSA PUBLIC KEY-----\r\n" + 
//					"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3feui3cgb2GzoHy3BL7C\r\n" + 
//					"ub3rRphGOGvE1YkC8HTXcE4hMhBYW6IlE+C5m9epwBQfWQKE3ZgAzDQCMYIoUubl\r\n" + 
//					"19mJAfDuATH25J8AsiIG4XYwHOTBOJEdvmCIRIHUli20kmBhoKfLgTuyypl3QUnA\r\n" + 
//					"2H3klw8pLQFp3tsre8svVbXHytfWdq7b8gtWBGrt7j75IY7dzeMYpmAaASobAdos\r\n" + 
//					"n5odkXWoxbEABIUuD0ZnAGjT8txg+tcFjMYEc5bhHPdTOxAnWk/8MmTMwPBCpmWW\r\n" + 
//					"NM/elpsjRRlItVMZT2I4xZAYfVp/5dMMA+4t4ISOaIyRqYFZITHyb7MjwcxdJR5H\r\n" + 
//					"AwIDAQAB\r\n" + 
//					"-----END RSA PUBLIC KEY-----");
		DeviceClient client = new DeviceClient("localhost", 50051);
			ByteString nonce = ByteString.copyFromUtf8("nonce");
			ByteString key = client.getPublicKey(nonce).getRSAEncryptionKey();
			LocalDate d1 = LocalDate.of(2017, 8, 1);
			Timestamp t1 = Timestamp.valueOf((d1.atStartOfDay()));
			LocalDate d2 = LocalDate.of(2017, 8, 12);
			Timestamp t2 =Timestamp.valueOf(d2.atStartOfDay());
			LocalDate d3 = LocalDate.of(2017, 7, 1);
			Timestamp t3 = Timestamp.valueOf(d3.atStartOfDay());
			LocalDate d4 = LocalDate.of(2017, 7, 11);
			Timestamp t4 = Timestamp.valueOf(d4.atStartOfDay());
//			Path path = Paths.get("C:\\Users\\Dom\\Desktop\\Location Files\\PlainText\\domfraise1.txt");
//			byte[] file1 = Files.readAllBytes(path);
//			file1=Encryption.encrypt(key, file1);
//			insertFileAtDate(conn, user, file1, t1);
//			Path path2 = Paths.get("C:\\Users\\Dom\\Desktop\\Location Files\\PlainText\\domfraise2.txt");
//			byte[] file2 = Files.readAllBytes(path2);
//			file2=Encryption.encrypt(key, file2);
//			insertFileAtDate(conn, user, file2, t2);
//			Path path3 = Paths.get("C:\\Users\\Dom\\Desktop\\Location Files\\PlainText\\domfraise3.txt");
//			byte[] file3 = Files.readAllBytes(path3);
//			file3=Encryption.encrypt(key, file3);
//			insertFileAtDate(conn, user, file3, t3);
			Path path4 = Paths.get("C:\\Users\\Dom\\Desktop\\Location Files\\PlainText\\domfraise4.txt");
			byte[] file4 = Files.readAllBytes(path4);
			file4=Encryption.encrypt(key, file4);
			insertFileAtDate(conn, user, file4, t4);
//
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	//	@Test
	public void insertRequests(){

		try {
			PreparedStatement nodes = conn.prepareStatement("INSERT INTO Requests (company,employee,hash,reason, timestamp) VALUES(?,?,?,?,?)");
			nodes.setString(1, "Google");
			nodes.setString(2, "dave");
			nodes.setString(3, hashFile("C:\\Users\\Dom\\Documents\\myfile1.txt"));
			nodes.setString(4, "I think he's a terrorist");
			nodes.setTimestamp(5, getTimestamp());
			nodes.executeUpdate();

			nodes.setString(1, "Google");
			nodes.setString(2, "alan");
			nodes.setString(3, hashFile("C:\\Users\\Dom\\Documents\\myfile4.txt"));
			nodes.setString(4, "I think he's a flight risk");
			nodes.setTimestamp(5, getTimestamp());
			nodes.executeUpdate();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void slelc(){
		try {
			insertEntry(conn, "1");
			insertEntry(conn, "2");
			insertEntry(conn, "3");
			insertEntry(conn, "4");
			insertEntry(conn, "5");
			insertEntry(conn, "6");
			ArrayList<String> actual = getLeaves(conn);
			ArrayList<String> expected = new ArrayList<String>();
			expected.add("1");
			expected.add("2");
			expected.add("3");
			expected.add("4");
			expected.add("5");
			expected.add("6");
			assertEquals(expected,actual);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetMyFiles(){
		try {
			ArrayList<String> files = getMyFiles(conn, "domfraise");
			ArrayList<String> expected = new ArrayList<String>();
			Path path = Paths.get("C:\\Users\\Dom\\Documents\\myfile1.txt");
			byte[] file = Files.readAllBytes(path);
			expected.add(hash(file));
			path = Paths.get("C:\\Users\\Dom\\Documents\\myfile2.txt");
			file = Files.readAllBytes(path);
			expected.add(hash(file));
			path = Paths.get("C:\\Users\\Dom\\Documents\\myfile3.txt");
			file = Files.readAllBytes(path);
			expected.add(hash(file));
			path = Paths.get("C:\\Users\\Dom\\Documents\\myfile4.txt");
			file = Files.readAllBytes(path);
			expected.add(hash(file));
			path = Paths.get("C:\\Users\\Dom\\Documents\\myfile5.txt");
			file = Files.readAllBytes(path);
			expected.add(hash(file));
			assertEquals(expected, files);
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetRequest(){
		try {
			System.out.println(hash(""));
			String[] actual = getRequest(conn, hashFile("C:\\Users\\Dom\\Documents\\myfile1.txt"));
			String [] expected = new String[5];
			expected[0] = "Google";
			expected[1] = "dave";
			expected[2] = hashFile("C:\\Users\\Dom\\Documents\\myfile1.txt");
			expected[3] = "I think he's a terrorist";

			for(int i=0;i<4;i++){
				assertEquals(expected[i], actual[i]);
			}
			System.out.println(actual[4]);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testadminSearch(){
		try {
			ArrayList<String[]> actual = adminSearch(conn, "domfraise", "2017-07-06","2017-07-06", "13:54","13:56");
			for(String[] i: actual){
				System.out.println((i[1]));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void resetDatabase(){
		PreparedStatement del;
		try {
			del = conn.prepareStatement("Delete FROM Tree");
			del.executeUpdate();
			del = conn.prepareStatement("Delete FROM log");
			del.executeUpdate();
			del = conn.prepareStatement("Delete FROM roots");
			del.executeUpdate();
			del = conn.prepareStatement("Delete FROM requests");
			del.executeUpdate();
			insertEntry(conn, hash(""));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		//	@After
//	@Test
		public void removeEntries(){
			PreparedStatement del;
			try {
				del = conn.prepareStatement("Delete FROM Tree");
				del.executeUpdate();
				del = conn.prepareStatement("Delete FROM log");
				del.executeUpdate();
				del = conn.prepareStatement("Delete FROM roots");
				del.executeUpdate();
				del = conn.prepareStatement("Delete FROM requests");
				del.executeUpdate();
				del = conn.prepareStatement("Delete FROM files");
				del.executeUpdate();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}



	}
