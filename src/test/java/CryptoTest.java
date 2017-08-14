import org.junit.Test;

import DeviceRPC.DeviceClient;
import junit.framework.TestCase;

public class CryptoTest extends TestCase {
	String key = crypto.Encryption.stripKey("-----BEGIN RSA PUBLIC KEY-----\r\n" + 
			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3feui3cgb2GzoHy3BL7C\r\n" + 
			"ub3rRphGOGvE1YkC8HTXcE4hMhBYW6IlE+C5m9epwBQfWQKE3ZgAzDQCMYIoUubl\r\n" + 
			"19mJAfDuATH25J8AsiIG4XYwHOTBOJEdvmCIRIHUli20kmBhoKfLgTuyypl3QUnA\r\n" + 
			"2H3klw8pLQFp3tsre8svVbXHytfWdq7b8gtWBGrt7j75IY7dzeMYpmAaASobAdos\r\n" + 
			"n5odkXWoxbEABIUuD0ZnAGjT8txg+tcFjMYEc5bhHPdTOxAnWk/8MmTMwPBCpmWW\r\n" + 
			"NM/elpsjRRlItVMZT2I4xZAYfVp/5dMMA+4t4ISOaIyRqYFZITHyb7MjwcxdJR5H\r\n" + 
			"AwIDAQAB\r\n" + 
			"-----END RSA PUBLIC KEY-----");
//	DeviceRPC.DeviceClient client = new DeviceClient("localhost",50051);
//	@Test
//	public void testEnc(){
//		System.out.println(key);
//		byte[] pt = "hello".getBytes();
//		byte[] ct = crypto.Encryption.encrypt(key, pt);
//		
//		
//		
//	}
	@Test
	public void testKeyStrip(){
		String key = "-----BEGIN RSA PUBLIC KEY-----\r\n" + 
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3feui3cgb2GzoHy3BL7C\r\n" + 
				"ub3rRphGOGvE1YkC8HTXcE4hMhBYW6IlE+C5m9epwBQfWQKE3ZgAzDQCMYIoUubl\r\n" + 
				"19mJAfDuATH25J8AsiIG4XYwHOTBOJEdvmCIRIHUli20kmBhoKfLgTuyypl3QUnA\r\n" + 
				"2H3klw8pLQFp3tsre8svVbXHytfWdq7b8gtWBGrt7j75IY7dzeMYpmAaASobAdos\r\n" + 
				"n5odkXWoxbEABIUuD0ZnAGjT8txg+tcFjMYEc5bhHPdTOxAnWk/8MmTMwPBCpmWW\r\n" + 
				"NM/elpsjRRlItVMZT2I4xZAYfVp/5dMMA+4t4ISOaIyRqYFZITHyb7MjwcxdJR5H\r\n" + 
				"AwIDAQAB\r\n" + 
				"-----END RSA PUBLIC KEY-----";
		key =crypto.Encryption.stripKey(key);
		
	}
}
