import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ByteString;

import DeviceRPC.DeviceClient;
import crypto.Encryption;
import io.grpc.decryptiondevice.Quote;
import io.grpc.decryptiondevice.RootTreeHash;
import javax.crypto.Cipher;

public class RPCTest {

	DeviceClient client; 
	ByteString nonce;
	@Before
	public void setup(){
		client = new DeviceClient("localhost", 50051);
		nonce = ByteString.copyFromUtf8("nonce");
	}
	@Test
	public void testGetKey() {
		Quote key = client.getPublicKey(nonce);
		System.out.println(key.getRSAVerificationKey().toStringUtf8());
	}
	
	@Test
	public void encDec(){
		ByteString key = client.getPublicKey(nonce).getRSAEncryptionKey();
//		System.out.println(key);
		byte[] pt;
		try {
			pt = "hellocunt".getBytes();
			ByteString ct = ByteString.copyFrom(crypto.Encryption.encrypt(key, pt));
			byte[] response = client.decryptRecord("{Proof:\"proof\"}", "{Proof:\"proof\"}", ct).getPlaintext().toByteArray();
			System.out.println(new String(response, "US-ASCII"));
			assertArrayEquals(pt, response);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void encDecJavaOnly() throws IOException{
		ByteString publicKey = client.getPublicKey(nonce).getRSAEncryptionKey();
		
//		PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream("crypt.pem")));
	    String strKeyPEM = "";
	    BufferedReader br = new BufferedReader(new FileReader("crypt_pkcs8.pem"));
	    String line;
	    while ((line = br.readLine()) != null) {
	        strKeyPEM += line+"\n";
	    }
	    br.close();

		byte[] pt;
		try {
			pt = "h".getBytes("UTF-8");
			byte[] ct = crypto.Encryption.encrypt(publicKey, pt);
			byte[] response= crypto.Encryption.decrypt(strKeyPEM, ct);
			
			assertArrayEquals(pt, response);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetRTH(){
		RootTreeHash response = client.getRootTreeHash(nonce);
		String RTH;
		
//			RTH = new String( response.getRth().toByteArray(),"UTF-8");
		    
			String rth  = Base64.getEncoder().encodeToString(response.getRth().toByteArray());
			String sig = Base64.getEncoder().encodeToString(response.getSig().toByteArray());
			System.out.println(rth);
			System.out.println(sig);
		
	}
	
	
	/**
	 * -----BEGIN RSA PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Z5GYM7m4KxAhX/vQ3aE
AXtlDlw2gVNwQ0UmiP1fH3G6ElaWTgznYK7cgtK92jGAimAkwH6nxqrl2S4DtwNY
XIrbJOBGxYOF1PloW7Gb2wAZb7T6D8SXdWZUB/uI+K6l/hDxRMrowZRLgSQi/WBc
uEBFf8NM/SQfv1jfVEb0ExnPsax3HZkMmujB+sNzwnGvieNCJtRrqnMDH0qIfH//
VqUaDt5BCF3OuWsit7WJgcqpaxoybkr5ppQsGweBDCJHM/3+UIaJzNKt+G5usHDa
XZwqwi/Gmqi5Dj7gUiskGNFaYQvANbfiEnynN48GKHYIkllQxN887yymvoyuSy6i
JwIDAQAB
-----END RSA PUBLIC KEY-----
	 */
	@After
	public void destroy(){
		try {
			client.shutdown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
