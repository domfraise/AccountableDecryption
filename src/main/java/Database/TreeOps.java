package Database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TreeOps {
	public static ArrayList<String> proofOfPresence = new ArrayList<String>();
	
	public static String hash(String s){
		MessageDigest md;
		byte[] digest = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String text = s;
			md.update(text.getBytes("UTF-8"));
			digest = md.digest();
			return String.format("%064x", new java.math.BigInteger(1, digest));

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("Hashing error");
		}
		return null;
	}
	public static String hash(byte[] f){
		MessageDigest md;
		byte[] digest = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(f);
			digest = md.digest();
			return String.format("%064x", new java.math.BigInteger(1, digest));

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.out.println("Hashing error");
		}
		return null;
	}

	/**
	 * Adds an entry to the merkel tree as a leaf nodes, updates all intermediary nodes
	 * @param t Tree to be extended
	 * @param v value to be added
	 * @return tree t extended with value v
	 */
	public static Tree extend(Tree t, String v){
		if(t.isEmpty()){
			return new Tree(v);
		}else if(t.getNumNodes() == Math.pow(2, t.getHeight())-1){//if tree is full
			//			proofOfPresence.add(t.getValue()+" height = "+t.getHeight());
			return new Tree(hash(t.getValue().concat(v)),t,new Tree(v));
		}else{
			Tree newRight = extend(t.getRight(),v);
			//			proofOfPresence.add(t.getLeft().getValue()+" height = "+t.getHeight());
			return new Tree(hash(t.getLeft().getValue().concat(newRight.getValue())),t.getLeft(),newRight);
		}
	}


	/**
	 * produces the tree into a array list in bfs order
	 * @param t
	 * @return
	 */
	public static ArrayList<String> bfsTraveral(Tree t){
		Queue<Tree> q = new LinkedList<Tree>();
		ArrayList<String> vals = new ArrayList<String>();
		if(t.isEmpty()){
			return vals;
		}else{
			q.add(t);
			while(!q.isEmpty()){
				Tree temp = q.remove();
				vals.add(temp.getValue());
				if(!temp.getLeft().isEmpty()){
					q.add(temp.getLeft());
				}
				if(!temp.getRight().isEmpty()){
					q.add(temp.getRight());
				}
			}
		}
		return vals;
	}



	/**
	 * gets proof of presence of an entry in a merkel tree
	 * one value for each sibling going up the tree
	 * proof consists of the sibling and if the sibling is on the left or right hand side
	 * @param t tree that should have the endtry in
	 * @param n index of the leaf node to retrieve proof for counting from 0
	 */
	public static ArrayList<String> provePresence(Tree t,int n){
		//		ArrayList<String> treeList = bfsTraveral(t);
		ArrayList<String> proofs = new ArrayList<String>();
		Tree temp = t;
		int counter = 0; //sums all powers of two calulated
		int currentPowOf2;
		while(!temp.getLeft().isEmpty() && !temp.getRight().isEmpty()){
			currentPowOf2 = temp.getPowOf2();
			if(currentPowOf2 == temp.getNumLeaves()){//checks for a full tree
				if(n+1>currentPowOf2/2+counter){
					String proof = temp.getLeft().getValue();
					proofs.add(proof);
					temp = temp.getRight();
					counter+=currentPowOf2/2;
				}else{
					String proof = temp.getRight().getValue();
					proofs.add(proof);
					temp = temp.getLeft();
				}
			}
			else if(n+1>currentPowOf2+counter){
				String proof = temp.getLeft().getValue();
				proofs.add(proof);
				temp = temp.getRight();
				counter+=currentPowOf2;
			}else{
				String proof = temp.getRight().getValue();
				proofs.add(proof);
				temp = temp.getLeft();
			}
		}
		return proofs;

	}
	
	/**
	 * provides proof that a tree with elements inserted is an append only version of the old tree
	 * @param t Tree. the Original tree before insersion of elements
	 * @param String[] entries the entries that are to be inserted into the tree
	 * @return ArrayList<String[]> with each entry being a node with a hash and the height that that node resides in the new tree
	 */
	public static ArrayList<String> proveExtension(Tree t, String[] entries){
		ArrayList<String> proofs = new ArrayList<String>();
	
		if (entries.length == 0) return proofs;
		int spaces = t.getPowOf2()*2-t.getNumLeaves(); //spaces left to complete current tree and create another of same size
		if(t.getNumLeaves() == t.getPowOf2()){     //symetric case
			String proof1 = t.getValue();
			proofs.add(proof1);
			Tree additions = new Tree();
			
			for(int i = 0;i< entries.length && i< spaces;i++){
				additions = TreeOps.extend(additions, entries[i]);
			}
			String proof2 = additions.getValue();
			proofs.add(proof2);
		}else{								//asymetric case
			Tree temp = t;
			while(temp.getNumLeaves() != temp.getPowOf2()){
				String proof1 = temp.getLeft().getValue();
				proofs.add(proof1);
				temp = temp.getRight();
			}
			String proof2 = temp.getValue();
			proofs.add(proof2);
			Tree additions = new Tree();
			for(int i = 0;i< entries.length && i< spaces;i++){
				additions = TreeOps.extend(additions, entries[i]);
			}
			String proof3 = additions.getValue();
			proofs.add(proof3);
		}
		
		if(entries.length > spaces){ //case where more entries are being added than already exist in tree
			int nextEntryIndex  = spaces;
			
//			int newTreeSpaces = t.getPowOf2()*2;
			Tree oldTree = t;//might need to be filled till symmetrical
			
			while (nextEntryIndex < entries.length){
				int newTreeSpaces = oldTree.getPowOf2()*2;
				Tree newTree = new Tree();
				for(int i = nextEntryIndex;i<entries.length && i-nextEntryIndex <newTreeSpaces;i++){
					newTree = TreeOps.extend(newTree, entries[i]);
				}
				String proof = newTree.getValue();
				proofs.add(proof);
				nextEntryIndex = nextEntryIndex + newTree.getPowOf2();
				oldTree = newTree;
				
			}
		}

		return proofs;
	}
	
	/**
	 * Recusive method to create json tree with proof of presence for a leaf
	 * @param t new tree
	 * @param proofs list of proof nodes
	 * @return a Json tree
	 */
	public static JSONObject jsonProof(Tree newTree,ArrayList<String> proofs){
		if(newTree.getLeft().isEmpty() && newTree.getRight().isEmpty() && !proofs.contains(newTree.getValue())) {
			JSONObject leaf = new JSONObject();
			leaf.put("Leaf", newTree.getValue());
			return leaf;
		}
	
		if(proofs.contains(newTree.getValue())){
			JSONObject hash = new JSONObject();
			hash.put("Hash", newTree.getValue());
			return hash;
		}else{
			JSONObject tree = new JSONObject();
			tree.put("Left", jsonProof(newTree.getLeft(),proofs));
			tree.put("Right", jsonProof(newTree.getRight(), proofs));
			return tree;
		}
	}
	
	public static void generateProofOfPresence(Tree t, int index,String filePath){
		ArrayList<String> proofNodes = provePresence(t,index);
		JSONObject proofTree = jsonProof(t,proofNodes);
		JSONObject fileContents = new JSONObject();
		fileContents.put("RTH", t.getValue());
		fileContents.put("LeafIndex", index);
		fileContents.put("Proof", jsonProof(t,proofNodes));
		
		try (FileWriter file = new FileWriter(filePath)) {
        	

            file.write(fileContents.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	public static JSONObject singleExtensionOld(Tree oldTree){
		if (oldTree.isEmpty()) return new JSONObject();
		if(oldTree.getNumLeaves() == oldTree.getPowOf2()){
			JSONObject proof = new JSONObject();
//			JSONObject left = new JSONObject();
			if(oldTree.getNumLeaves() !=1){
				proof.put("Hash", oldTree.getValue());
			}else{
				JSONObject left = new JSONObject();
				left.put("Hash", oldTree.getValue());
				proof.put("Left", left);
			}
//			proof.put("Left", left);
			return proof;
		}else{
			JSONObject proof = new JSONObject();
			proof.put("Left", singleExtensionOld(oldTree.getLeft()));
			proof.put("Right", singleExtensionOld(oldTree.getRight()));
			return proof;
		}
	}
	
	public static JSONObject singleExtensionNew(Tree newTree){
		if (newTree.getLeft().isEmpty() && newTree.getRight().isEmpty()){
			JSONObject proof = new JSONObject();
			proof.put("Hash", newTree.getValue());
			return proof;
		}else{
			JSONObject proof = new JSONObject();
			proof.put("Right", singleExtensionNew(newTree.getRight()));
			return proof;
		}
	}
	
	public static void generateSingleExtensionProof(Tree oldTree, Tree newTree,String filePath){
		if(Math.abs(oldTree.getNumLeaves()-newTree.getNumLeaves()) > 1) throw new IllegalArgumentException("New tree must be only 1 element larger than old tree");
		JSONObject oldProof = singleExtensionOld(oldTree);
		JSONObject newProof = singleExtensionNew( newTree);
		JSONObject fileContents = new JSONObject();
		if(oldTree.isEmpty()){
			fileContents.put("OldRTH", null);

		}else{
			fileContents.put("OldRTH", oldTree.getValue());
		}
		fileContents.put("NewRTH", newTree.getValue());
		fileContents.put("OldProof", oldProof);
		fileContents.put("NewProof", newProof);
		
		
		try (FileWriter file = new FileWriter(filePath)) {
        	

            file.write(fileContents.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
//	public static void writeProofsToJson(String filePath, Tree original,Tree newTree,String[] valuesToAdd){
//	
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("OldRoot", original.getValue());
//		jsonObj.put("NewRoot", newTree.getValue());
//
//		JSONArray proofOfPresenceArray = new JSONArray();
//		for(int i = 0; i<valuesToAdd.length;i++){
//			int index = original.getNumLeaves()+i;
//			JSONObject proof = new JSONObject();
//			proof.put("LeafIndex", index);
//			proof.put("LeafValue", valuesToAdd[i]);
//			JSONArray nodes = new JSONArray();
//			ArrayList<String> proofNodes = provePresence(newTree, index);
//			
//			for(String line: proofNodes){
//				JSONObject node = new JSONObject();
//				node.put("Hash", line);
//				node.put("Position", line);
//		
//				nodes.add(node);
//			}
//			proof.put("Nodes", nodes);
//			proofOfPresenceArray.add(proof);
//		}
//		jsonObj.put("ProofsOfPresence", proofOfPresenceArray);
//		
//		//proof of extesnion
//		
//		JSONArray values = new JSONArray();
//		for(String val: valuesToAdd){
//			values.add(val);
//		}
//	
//		ArrayList<String> proofs = proveExtension(original, valuesToAdd);
//	
//		
//		JSONObject proofOfExtension = new JSONObject();
//		proofOfExtension.put("ValuesAdded", values);
//		proofOfExtension.put("ProofTree", jsonConversion(newTree, proofs));
//		jsonObj.put("ProofOfExtension", proofOfExtension);
//		
//		jsonObj.put("ProofOfExtension", proofOfExtension);
//		
//		
//
//
//        try (FileWriter file = new FileWriter(filePath)) {
//        	
//
//            file.write(jsonObj.toJSONString());
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		
//	}
}
