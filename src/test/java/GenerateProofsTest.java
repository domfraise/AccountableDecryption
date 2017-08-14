import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import static Database.TreeOps.*;
import static Database.Proofs.*;

import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static Database.Database.*;
import com.google.protobuf.ByteString;

import Database.DecryptionRequest;
import DeviceRPC.DeviceClient;

public class GenerateProofsTest {
	ArrayList<String[]> files;
	Connection conn;
	DeviceClient client = new DeviceClient("localhost", 50051);
	ByteString nonce = ByteString.copyFromUtf8("nonce");
	String key = crypto.Encryption.stripKey(client.getPublicKey(nonce).getRSAEncryptionKey().toStringUtf8());
	@Before
	public void setup(){
		String line;
		String splitter=",";
		String[] token;

		files=new ArrayList<String[]>();
		try {
			conn = Database.Database.getConnection();
			BufferedReader br=new BufferedReader(new FileReader("records.csv"));
			while((line=br.readLine())!=null){
				token=line.split(splitter); 
				files.add(token);
				Database.Database.insertFileAsBytes(conn, "domfraise", Base64.getDecoder().decode(token[1]));

			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void getAllProofs(){
		DecryptionRequest req;
		String allProofs = ""; 
		try {
			String oldRoot = getRoot(conn);
			for(String[] i: files){
				byte[] file = Base64.getDecoder().decode(i[1]);
				String fileHash = hash(file);
				allProofs = allProofs.concat(fileHash+",");
				req = new DecryptionRequest(conn, "Google", "JaneSmith", fileHash, "reasons");
				req.addToDatabase();
				String newRoot = getRoot(conn);
				allProofs = allProofs.concat(req.getProofOfPresence().toJSONString()+",");
				allProofs = allProofs.concat(proveExtension(conn, oldRoot, newRoot).toJSONString()+"\n");
				
				
				
				
			}
			writeStringToFile(allProofs, "C:\\Users\\Dom\\Desktop\\all512Decrypted.txt");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
//	public void test() {
//		System.out.println(client.decryptRecord("proof", "proof", ByteString.copyFrom(Base64.getDecoder().decode(files.get(0)[1]))).getPlaintext().toStringUtf8());
//		assertEquals(Base64.getEncoder().encodeToString(crypto.Encryption.encrypt(key, files.get(0)[0].getBytes())), files.get(0)[1]);
//	}

	@After
	public void after(){
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
//			del = conn.prepareStatement("Delete FROM files");
//			del.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
