package DeviceRPC;


import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import DeviceRPC.DecryptionDeviceGrpc.DecryptionDeviceBlockingStub;
import DeviceRPC.DecryptionDeviceGrpc.DecryptionDeviceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.decryptiondevice.DecryptionDeviceProto;
import io.grpc.decryptiondevice.PublicKeyRequest;
//import io.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideBlockingStub;
//import io.grpc.examples.routeguide.RouteGuideGrpc.RouteGuideStub;
import io.grpc.stub.StreamObserver;

import java.awt.Point;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.grpc.decryptiondevice.*;
import com.google.protobuf.ByteString;

public class DeviceClient {
	private static final Logger logger = Logger.getLogger(DeviceClient.class.getName());

	private final ManagedChannel channel;
	private final DecryptionDeviceBlockingStub blockingStub;
	private final DecryptionDeviceStub asyncStub;

	private Random random = new Random();
	//	private TestHelper testHelper;
	public DeviceClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
	}
	/** Construct client for accessing DecryptionDevice server using the existing channel. */
	public DeviceClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
		blockingStub = DecryptionDeviceGrpc.newBlockingStub(channel);
		asyncStub = DecryptionDeviceGrpc.newStub(channel);
	}
	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}
	/**
	 * Blocking unary call example.  Calls getFeature and prints the response.
	 */
	public Quote getPublicKey(ByteString nonce) {
		PublicKeyRequest keyRequest = PublicKeyRequest.newBuilder().setNonce(nonce).build();
		try{
			 return blockingStub.getPublicKey(keyRequest);
			
		}catch(StatusRuntimeException e){
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			  return null;
		}
	}

	public RootTreeHash getRootTreeHash(ByteString nonce){
		RootTreeHashRequest request = RootTreeHashRequest.newBuilder().setNonce(nonce).build();
		try{
			 return blockingStub.getRootTreeHash(request);
			
		}catch(StatusRuntimeException e){
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			  return null;
		}
	}

	public Record decryptRecord(String proofPresence,String proofExtesnsion, ByteString ciphertext){
		DecryptionRequest request = DecryptionRequest.newBuilder().setProofOfExtension(proofExtesnsion)
				.setProofOfPresence(proofPresence).setCiphertext(ciphertext).build();
		System.out.println("build request - clientrpc");
		try{
			 return blockingStub.decryptRecord(request);
			
		}catch(StatusRuntimeException e){
			  logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			  return null;
		}
	}



	public static void main(String[] args) throws InterruptedException {
		ByteString nonce = ByteString.copyFromUtf8("gfdgdg");
		DeviceClient client = new DeviceClient("localhost", 50051);
		try {

			Quote key = client.getPublicKey(nonce);
			RootTreeHash RTH = client.getRootTreeHash(nonce);
			
			System.out.println("PK:"+key.getRSAEncryptionKey().toStringUtf8()+" VK:"+key.getRSAVerificationKey().toStringUtf8());
			System.out.println("RTH:"+RTH.getRth()+" Sig:"+RTH.getSig().toStringUtf8());

		} finally {
			client.shutdown();
		}
	}



	/**
	 * Only used for unit test, as we do not want to introduce randomness in unit test.
	 */
	@VisibleForTesting
	void setRandom(Random random) {
		this.random = random;
	}

	/**
	 * Only used for helping unit test.
	 */
	@VisibleForTesting
	interface TestHelper {
		/**
		 * Used for verify/inspect message received from server.
		 */
		void onMessage(Message message);

		/**
		 * Used for verify/inspect error received from server.
		 */
		void onRpcError(Throwable exception);
	}
	private void info(String msg, Object... params) {
		logger.log(Level.INFO, msg, params);
	}
}
