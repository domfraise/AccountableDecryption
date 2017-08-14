package Database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static Database.Database.getPowOf2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Proofs {


	/**
	 * Calls a recusive method to get a JSON tree containing the proof of presence for an elemnt in a tree
	 * @param rth String The root tree hash of the tree to be checked
	 * @param i the index of the entry in the log
	 * @return JSONObject A tree with hash values and positions of the proof nodes in the tree
	 * @throws SQLException Thrown in cannot connect to the database
	 */
	public static JSONObject provePresence(String rth,int i) throws SQLException{
		Connection conn = Database.getConnection();
		PreparedStatement getLeaf = conn.prepareStatement("SELECT * FROM log WHERE index = ?");
		getLeaf.setInt(1, i);
		ResultSet leaf = getLeaf.executeQuery();
		if(!leaf.isBeforeFirst()){
			System.out.println("No entry found in the log at index "+i);
			return null;
		}
		leaf.next();
		String leaf_value = leaf.getString(1);
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM Tree WHERE node = ? ");
		getNode.setString(1, rth);
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			System.out.println("No root with value "+rth+" found");
			return null;
		}
		node.next();
		try{
			JSONObject proofTree =  provePresenceRec(conn, rth, leaf_value, i, 0,node.getInt(4));
			JSONObject proofFile = new JSONObject();
			proofFile.put("RTH", rth);
			proofFile.put("Index", i);
			proofFile.put("Value", leaf_value);
			proofFile.put("Proof", proofTree);
			return proofFile;
		}catch(NullPointerException e){
			return null;
		}
		catch(SQLException e){
			return null;
		}
		finally{
			conn.close();
		}
	}
	
	/**
	 * Calls a recusive method to get a JSON tree containing the proof of presence for an elemnt in a tree
	 * @param rth String The root tree hash of the tree to be checked
	 * @param i the index of the entry in the log
	 * @return JSONObject A tree with hash values and positions of the proof nodes in the tree
	 * @throws SQLException Thrown in cannot connect to the database
	 */
	public static JSONObject provePresence(String rth,String leaf_value) throws SQLException{
		Connection conn = Database.getConnection();
//		PreparedStatement getLeaf = conn.prepareStatement("SELECT * FROM log WHERE index = ?");
//		getLeaf.setInt(1, i);
//		ResultSet leaf = getLeaf.executeQuery();
//		if(!leaf.isBeforeFirst()){
//			System.out.println("No entry found in the log at index "+i);
//			return null;
//		}
//		leaf.next();
//		String leaf_value = leaf.getString(1);
		int i = Database.getIndex(conn, leaf_value);
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM roots WHERE RTH LIKE ? ");
		getNode.setString(1, rth);
		ResultSet node = getNode.executeQuery();
		if(!node.isBeforeFirst()){
			System.out.println("No root with value "+rth+" found");
			return null;
		}
		node.next();
		try{
			JSONObject proofTree =  provePresenceRec(conn, rth, leaf_value, i, 0,node.getInt(4));
			JSONObject proofFile = new JSONObject();
			proofFile.put("RTH", rth);
			proofFile.put("Index", i);
			proofFile.put("Value", leaf_value);
			proofFile.put("Proof", proofTree);
			return proofFile;
		}catch(NullPointerException e){
			return null;
		}
		catch(SQLException e){
			return null;
		}
		finally{
			conn.close();
		}
	}



	/**
	 * Recursive call for calulateing proof of presence
	 * @param conn Database connection
	 * @param nodeValue hash value at current node
	 * @param leaf Hash value of the leaf to be searched for
	 * @param index index of leaf in log
	 * @param offset counts how many leaf nodes have been ignored by going down a right branch. i.e. 8 leaves => go right => offset =4
	 * @param size number of leaves in current node
	 * @return JSONObject  json proof tree
	 * @throws SQLException
	 */
	private static JSONObject provePresenceRec(Connection conn,String nodeValue,String leaf,int index,int offset,int size) throws SQLException,NullPointerException{
		PreparedStatement getNode = conn.prepareStatement("SELECT * FROM Tree WHERE node = ? ");
		getNode.setString(1, nodeValue);
		ResultSet node = getNode.executeQuery();node.next();
		String left = node.getString(2);
		String right= node.getString(3);
		//tree only consists of one node
		if(nodeValue.equals(leaf)){
			return new JSONObject();
		}
		else if(leaf.equals(left)){
			JSONObject leafProof = new JSONObject();
			JSONObject hash = new JSONObject();
			hash.put("Hash", right);
			leafProof.put("Right", hash);
			JSONObject leafHash = new JSONObject();
			leafHash.put("Leaf", leaf);
			leafProof.put("Left", leafHash);
			return leafProof;
		}
		else if(leaf.equals(right)){
			JSONObject leafProof = new JSONObject();
			JSONObject hash = new JSONObject();
			hash.put("Hash", left);
			leafProof.put("Left", hash);
			JSONObject leafHash = new JSONObject();
			leafHash.put("Leaf", leaf);
			leafProof.put("Right", leafHash);
			return leafProof;
		}
		//recursive case
		else{
			//leaf is in the right branch
			if(index-offset >=getPowOf2(size)/2){
				JSONObject leafProof = new JSONObject();
				JSONObject hash = new JSONObject();
				hash.put("Hash", left);
				leafProof.put("Left", hash);
				leafProof.put("Right", provePresenceRec(conn, right, leaf, index, offset+getPowOf2(size)/2,size-getPowOf2(size)/2));
				return leafProof;
			}else{//leaf is in left branch
				JSONObject leafProof = new JSONObject();
				JSONObject hash = new JSONObject();
				hash.put("Hash", right);
				leafProof.put("Left", provePresenceRec(conn, left, leaf, index, offset,getPowOf2(size)/2));
				leafProof.put("Right",  hash);
				return leafProof;
			}
		}
	}
	
	/**
	 * Provides proof of extension in the form of 2 JSON trees within a JSONObject. One tree to reconstruct the original hash, and one to reconstruct the new hash.
	 * If it is not an extension method returns null
	 * @param conn Conncetion DB connection
	 * @param g String old root tree hash
	 * @param h String new root tree hash
	 * @return JSON OBject containing both RTH's and two proof trees
	 * @throws SQLException
	 */
	public static JSONObject proveExtension(Connection conn ,String g,String h) throws SQLException{
		
		JSONObject[] proofTrees;
		try{
			proofTrees= proveExtensionRec(conn, g, h);
		}catch(IllegalArgumentException e){
			
			return null;
		}
		JSONObject proofFile = new JSONObject();
		proofFile.put("OldRTH", g);
		proofFile.put("NewRTH", h);
		proofFile.put("OldProof", proofTrees[0]);
		proofFile.put("NewProof", proofTrees[1]);
		return proofFile;
	}

	/**
	 * Recusrive method for caluculating proof trees for extesnion 
	 * @param conn DB Connection
	 * @param g String old root tree hash
	 * @param h String new root tree hash
	 * @return JSONObject[] 2 element array. index 0 being old proof tree, index 1 new proof tree
	 * @throws SQLException
	 */
	private static JSONObject[] proveExtensionRec(Connection conn ,String g,String h) throws SQLException{
		JSONObject s= new JSONObject();
		JSONObject t= new JSONObject();

		JSONObject[] proof = new JSONObject[2];
		if(g.equals(h)){
			s.put("Hash", g);
			proof[0] = s;
			t.put("Hash", g);

			proof[1] = t;

			return proof;
		}
		ResultSet nodeG = Database.getNode(conn,g);
		String gLeft,gRight;
		int lenG;
		if(!nodeG.isBeforeFirst()){
			gLeft = "";
			gRight = "";
			lenG = -1;
			
//			throw new IllegalArgumentException("node g : "+g+" not found in tree");
		}else{
			nodeG.next();
			gLeft = nodeG.getString(2);
			gRight = nodeG.getString(3);
			lenG = nodeG.getInt(5);
		}

		ResultSet nodeH = Database.getNode(conn,h);
		if(!nodeH.isBeforeFirst()){
			throw new IllegalArgumentException("node h : "+h+" not found in tree");
		}
		nodeH.next();
		String hLeft = nodeH.getString(2);
		String hRight = nodeH.getString(3);
		int lenH = nodeH.getInt(5);

		if(lenG == lenH){

//			if(!gLeft.equals(hLeft)){
//				throw new IllegalArgumentException("g.l != h.l");
//			}
			JSONObject[] nextProof;
			if(g.equals("")){
				JSONObject l = new JSONObject();
				JSONObject r = proveExtensionRec(conn, gRight, hRight)[1];
				nextProof = new JSONObject[2];
				nextProof[0] = l;
				nextProof[1] = r;
			}else{
				nextProof= proveExtensionRec(conn,gRight,hRight);
			}
			JSONObject hash = new JSONObject();hash.put("Hash", gLeft);
			s.put("Left", hash);
			s.put("Right",  nextProof[0]);
			proof[0] = s;
			if(g.equals("")){
				proof[0] = new JSONObject();
			}


			t.put("Left", hash);
			t.put("Right", nextProof[1]);
			proof[1] = t;


			return proof;
		}
		if(!(lenG <lenH)){
			throw new IllegalArgumentException("g.n !< h.n");
		}
		JSONObject[] nextProof = new JSONObject[2];
		if(g.equals("")){
			proof[0] = new JSONObject();
			proof[1] = new JSONObject();
			proof[1].put("Hash", h);
			return proof;

		}else{
			nextProof= proveExtensionRec(conn,g,hLeft);
			proof[0] = nextProof[0];
		}
	

		t.put("Left", nextProof[1]);

		JSONObject hash1 = new JSONObject();hash1.put("Hash", hRight);

		t.put("Right", hash1);
		proof[1] = t;

		return proof;

	}

	/**
	 * provides information on if a user files have been decryped
	 * @param conn
	 * @param user String Owner of files that may have been decrypted
	 * @return ArrayList<String[]> each entry is information on each decryption request made for this users files.
	 * each entry has this format [company,employee,hash of file,reason,timestamp of request]
	 * @throws SQLException
	 */
	public static ArrayList<String[]> getRequests(Connection conn,String user) throws SQLException{
		ArrayList<String> leaves = Database.getLeaves(conn);
		String root = Database.getRoot(conn);
		Tree tree = new Tree();
		for(String i:leaves){
			tree = TreeOps.extend(tree, i);
		}
		
		if(!root.equals(tree.getValue())){
			throw new IllegalStateException("Cannot recreate RTH from leaves");
		}
		
		ArrayList<String> myFiles = Database.getMyFiles(conn, user);
		System.out.println(myFiles.size());
		ArrayList<String> hits = new ArrayList<String>();
		for(String i: myFiles){
			if(leaves.contains(i)){
				hits.add(i);
			}
		}
		
		ArrayList<String[]> requests = new ArrayList<String[]>();
		for(String i: hits){
			String[] request = Database.getRequest(conn, i);
			requests.add(request);
		}
		return requests;
	}
	
	/**
	 * generates a json object for a user to verify absence them selves
	 * @param conn
	 * @param user
	 * @return
	 * @throws SQLException
	 */
	public static JSONObject proveAbsence(Connection conn,String user) throws SQLException{
		JSONObject container = new JSONObject();
		container.put("RTHFromDevice", Database.getRoot(conn));//TODO change to the authenic rth value
		container.put("DeviceSig", "SomeSigGoesHere");
		JSONArray leaves = new JSONArray();
		for(String i: Database.getLeaves(conn)){
			leaves.add(i);
		}
		container.put("Leaves", leaves);
		JSONArray files = new JSONArray();
		for(String i: Database.getMyFiles(conn, user)){
			files.add(i);
		}
		container.put("FileHashes", files);
		return container;
	}
	
	/**
	 * Writes a JSONObject to a file
	 * @param o JSONObject object to be written
	 * @param filePath full path to location to store file
	 */
	public static void writeJsonToFile(JSONObject o,String filePath){
		try (FileWriter file = new FileWriter(filePath)) {
			file.write(o.toJSONString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeStringToFile(String o,String filePath){
		try (FileWriter file = new FileWriter(filePath)) {
			file.write(o);
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
