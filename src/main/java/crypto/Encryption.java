package crypto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.google.protobuf.ByteString;

import javax.crypto.Cipher;

/**
 * Encryption and decryption Functionality
 * @author Dom
 *
 */

public class Encryption {
	
	/**
	 * Encrypts bytes with the response from a gRPC call to the decvice
	 * @param rawKey ByteString The unstripped response from the gRPC call getEncryptionKey
	 * @param message byte[] the bytes of message to be encrypted
	 * @return byte[] encrypted message
	 */
	public static byte[] encrypt(ByteString rawKey ,byte[] message){
		String strippedKey=stripKey(rawKey.toStringUtf8());

		byte[] keyBytes = Base64.getDecoder().decode(strippedKey);
		
		
		Cipher cipher_RSA;
		try {
			cipher_RSA = Cipher.getInstance("RSA");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			PublicKey pk = keyFactory.generatePublic(spec);
			
			
			cipher_RSA.init(Cipher.ENCRYPT_MODE, pk); 
			return cipher_RSA.doFinal(message);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
				BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Decrypts a message - only used for debuggings/testing
	 * @param rawKey String unstripped key read from pem file
	 * @param message byte[] encrypted message
	 * @return encrypted
	 */
	public static byte[] decrypt(String rawKey ,byte[] message){
		String strippedKey=stripPrivateKey(rawKey);
		
		byte[] keyBytes = Base64.getDecoder().decode(strippedKey);
		
		
		Cipher cipher_RSA;
		try {
			cipher_RSA = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			PrivateKey pk = keyFactory.generatePrivate(spec);
		

			cipher_RSA.init(Cipher.DECRYPT_MODE, pk); 
			return cipher_RSA.doFinal(message);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
				BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	

	/**
	 * Translates a byte array to a hex string
	 * @param bytes byte[] raw bytes
	 * @return hex representation of bytes
	 */
	public static String bytes2String(byte[] bytes) {
		StringBuilder string = new StringBuilder();
		for (byte b : bytes) {
			String hexString = Integer.toHexString(0x00FF & b);
			string.append(hexString.length() == 1 ? "0" + hexString : hexString);
		}
		return string.toString();
	}

	
	public static String stripKey(String key){
		key = key.replace("-----BEGIN RSA PUBLIC KEY-----\n", "");
		key = key.replace("-----END RSA PUBLIC KEY-----", "");
		key = key.replace("\n",	"");
//		key = key.replace("\n\r", "");
		//		key = key.replace(" ", "");

		return key;
	}
	
	public static String stripPrivateKey(String key){
		key = key.replace("-----BEGIN PRIVATE KEY-----", "");
		key = key.replace("-----END PRIVATE KEY-----", "");
		key = key.replace("\n",	"");
//		key = key.replace("\n\r", "");
		//		key = key.replace(" ", "");

		return key;
	}

}
