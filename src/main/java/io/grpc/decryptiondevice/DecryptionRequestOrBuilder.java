// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: main/java/DeviceRPC/decryptiondevice.proto

package io.grpc.decryptiondevice;

public interface DecryptionRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:decryptiondevice.DecryptionRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bytes ciphertext = 1;</code>
   */
  com.google.protobuf.ByteString getCiphertext();

  /**
   * <code>string proofOfPresence = 2;</code>
   */
  java.lang.String getProofOfPresence();
  /**
   * <code>string proofOfPresence = 2;</code>
   */
  com.google.protobuf.ByteString
      getProofOfPresenceBytes();

  /**
   * <code>string proofOfExtension = 3;</code>
   */
  java.lang.String getProofOfExtension();
  /**
   * <code>string proofOfExtension = 3;</code>
   */
  com.google.protobuf.ByteString
      getProofOfExtensionBytes();
}