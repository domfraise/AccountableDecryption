package DeviceRPC;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: decryptiondevice.proto")
public final class DecryptionDeviceGrpc {

  private DecryptionDeviceGrpc() {}

  public static final String SERVICE_NAME = "decryptiondevice.DecryptionDevice";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<io.grpc.decryptiondevice.DecryptionRequest,
      io.grpc.decryptiondevice.Record> METHOD_DECRYPT_RECORD =
      io.grpc.MethodDescriptor.<io.grpc.decryptiondevice.DecryptionRequest, io.grpc.decryptiondevice.Record>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "decryptiondevice.DecryptionDevice", "DecryptRecord"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              io.grpc.decryptiondevice.DecryptionRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              io.grpc.decryptiondevice.Record.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<io.grpc.decryptiondevice.RootTreeHashRequest,
      io.grpc.decryptiondevice.RootTreeHash> METHOD_GET_ROOT_TREE_HASH =
      io.grpc.MethodDescriptor.<io.grpc.decryptiondevice.RootTreeHashRequest, io.grpc.decryptiondevice.RootTreeHash>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "decryptiondevice.DecryptionDevice", "GetRootTreeHash"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              io.grpc.decryptiondevice.RootTreeHashRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              io.grpc.decryptiondevice.RootTreeHash.getDefaultInstance()))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<io.grpc.decryptiondevice.PublicKeyRequest,
      io.grpc.decryptiondevice.Quote> METHOD_GET_PUBLIC_KEY =
      io.grpc.MethodDescriptor.<io.grpc.decryptiondevice.PublicKeyRequest, io.grpc.decryptiondevice.Quote>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "decryptiondevice.DecryptionDevice", "GetPublicKey"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              io.grpc.decryptiondevice.PublicKeyRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              io.grpc.decryptiondevice.Quote.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DecryptionDeviceStub newStub(io.grpc.Channel channel) {
    return new DecryptionDeviceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DecryptionDeviceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DecryptionDeviceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DecryptionDeviceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DecryptionDeviceFutureStub(channel);
  }

  /**
   */
  public static abstract class DecryptionDeviceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Decryption Request RPC
     * Request contains ciphertext and proof
     * Returns the plaintext record
     * </pre>
     */
    public void decryptRecord(io.grpc.decryptiondevice.DecryptionRequest request,
        io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.Record> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_DECRYPT_RECORD, responseObserver);
    }

    /**
     * <pre>
     * Get Signed Root Tree Hash RPC
     * Caller provides a nonce
     * Returns a signed RTH and nonce
     * </pre>
     */
    public void getRootTreeHash(io.grpc.decryptiondevice.RootTreeHashRequest request,
        io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.RootTreeHash> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_ROOT_TREE_HASH, responseObserver);
    }

    /**
     * <pre>
     * Get Public key RPC
     * Returns a Remote attestation report containing the public key as user data
     * </pre>
     */
    public void getPublicKey(io.grpc.decryptiondevice.PublicKeyRequest request,
        io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.Quote> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_PUBLIC_KEY, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_DECRYPT_RECORD,
            asyncUnaryCall(
              new MethodHandlers<
                io.grpc.decryptiondevice.DecryptionRequest,
                io.grpc.decryptiondevice.Record>(
                  this, METHODID_DECRYPT_RECORD)))
          .addMethod(
            METHOD_GET_ROOT_TREE_HASH,
            asyncUnaryCall(
              new MethodHandlers<
                io.grpc.decryptiondevice.RootTreeHashRequest,
                io.grpc.decryptiondevice.RootTreeHash>(
                  this, METHODID_GET_ROOT_TREE_HASH)))
          .addMethod(
            METHOD_GET_PUBLIC_KEY,
            asyncUnaryCall(
              new MethodHandlers<
                io.grpc.decryptiondevice.PublicKeyRequest,
                io.grpc.decryptiondevice.Quote>(
                  this, METHODID_GET_PUBLIC_KEY)))
          .build();
    }
  }

  /**
   */
  public static final class DecryptionDeviceStub extends io.grpc.stub.AbstractStub<DecryptionDeviceStub> {
    private DecryptionDeviceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DecryptionDeviceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DecryptionDeviceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DecryptionDeviceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Decryption Request RPC
     * Request contains ciphertext and proof
     * Returns the plaintext record
     * </pre>
     */
    public void decryptRecord(io.grpc.decryptiondevice.DecryptionRequest request,
        io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.Record> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DECRYPT_RECORD, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get Signed Root Tree Hash RPC
     * Caller provides a nonce
     * Returns a signed RTH and nonce
     * </pre>
     */
    public void getRootTreeHash(io.grpc.decryptiondevice.RootTreeHashRequest request,
        io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.RootTreeHash> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_ROOT_TREE_HASH, getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get Public key RPC
     * Returns a Remote attestation report containing the public key as user data
     * </pre>
     */
    public void getPublicKey(io.grpc.decryptiondevice.PublicKeyRequest request,
        io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.Quote> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_PUBLIC_KEY, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DecryptionDeviceBlockingStub extends io.grpc.stub.AbstractStub<DecryptionDeviceBlockingStub> {
    private DecryptionDeviceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DecryptionDeviceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DecryptionDeviceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DecryptionDeviceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Decryption Request RPC
     * Request contains ciphertext and proof
     * Returns the plaintext record
     * </pre>
     */
    public io.grpc.decryptiondevice.Record decryptRecord(io.grpc.decryptiondevice.DecryptionRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_DECRYPT_RECORD, getCallOptions(), request);
    }

    /**
     * <pre>
     * Get Signed Root Tree Hash RPC
     * Caller provides a nonce
     * Returns a signed RTH and nonce
     * </pre>
     */
    public io.grpc.decryptiondevice.RootTreeHash getRootTreeHash(io.grpc.decryptiondevice.RootTreeHashRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_ROOT_TREE_HASH, getCallOptions(), request);
    }

    /**
     * <pre>
     * Get Public key RPC
     * Returns a Remote attestation report containing the public key as user data
     * </pre>
     */
    public io.grpc.decryptiondevice.Quote getPublicKey(io.grpc.decryptiondevice.PublicKeyRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_PUBLIC_KEY, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DecryptionDeviceFutureStub extends io.grpc.stub.AbstractStub<DecryptionDeviceFutureStub> {
    private DecryptionDeviceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DecryptionDeviceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DecryptionDeviceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DecryptionDeviceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Decryption Request RPC
     * Request contains ciphertext and proof
     * Returns the plaintext record
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.decryptiondevice.Record> decryptRecord(
        io.grpc.decryptiondevice.DecryptionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DECRYPT_RECORD, getCallOptions()), request);
    }

    /**
     * <pre>
     * Get Signed Root Tree Hash RPC
     * Caller provides a nonce
     * Returns a signed RTH and nonce
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.decryptiondevice.RootTreeHash> getRootTreeHash(
        io.grpc.decryptiondevice.RootTreeHashRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_ROOT_TREE_HASH, getCallOptions()), request);
    }

    /**
     * <pre>
     * Get Public key RPC
     * Returns a Remote attestation report containing the public key as user data
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.grpc.decryptiondevice.Quote> getPublicKey(
        io.grpc.decryptiondevice.PublicKeyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_PUBLIC_KEY, getCallOptions()), request);
    }
  }

  private static final int METHODID_DECRYPT_RECORD = 0;
  private static final int METHODID_GET_ROOT_TREE_HASH = 1;
  private static final int METHODID_GET_PUBLIC_KEY = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DecryptionDeviceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DecryptionDeviceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DECRYPT_RECORD:
          serviceImpl.decryptRecord((io.grpc.decryptiondevice.DecryptionRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.Record>) responseObserver);
          break;
        case METHODID_GET_ROOT_TREE_HASH:
          serviceImpl.getRootTreeHash((io.grpc.decryptiondevice.RootTreeHashRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.RootTreeHash>) responseObserver);
          break;
        case METHODID_GET_PUBLIC_KEY:
          serviceImpl.getPublicKey((io.grpc.decryptiondevice.PublicKeyRequest) request,
              (io.grpc.stub.StreamObserver<io.grpc.decryptiondevice.Quote>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class DecryptionDeviceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.grpc.decryptiondevice.DecryptionDeviceProto.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DecryptionDeviceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DecryptionDeviceDescriptorSupplier())
              .addMethod(METHOD_DECRYPT_RECORD)
              .addMethod(METHOD_GET_ROOT_TREE_HASH)
              .addMethod(METHOD_GET_PUBLIC_KEY)
              .build();
        }
      }
    }
    return result;
  }
}
