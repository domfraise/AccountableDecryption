// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: main/java/DeviceRPC/decryptiondevice.proto

package io.grpc.decryptiondevice;

public interface QuoteOrBuilder extends
    // @@protoc_insertion_point(interface_extends:decryptiondevice.Quote)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *some format.. to be defined later
   * </pre>
   *
   * <code>string quote = 1;</code>
   */
  java.lang.String getQuote();
  /**
   * <pre>
   *some format.. to be defined later
   * </pre>
   *
   * <code>string quote = 1;</code>
   */
  com.google.protobuf.ByteString
      getQuoteBytes();

  /**
   * <pre>
   *PEM formatted key 
   * </pre>
   *
   * <code>bytes RSA_EncryptionKey = 2;</code>
   */
  com.google.protobuf.ByteString getRSAEncryptionKey();

  /**
   * <code>bytes RSA_VerificationKey = 3;</code>
   */
  com.google.protobuf.ByteString getRSAVerificationKey();
}
