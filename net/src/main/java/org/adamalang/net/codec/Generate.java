package org.adamalang.net.codec;

import org.adamalang.common.codec.CodecCodeGen;

import java.io.File;
import java.nio.file.Files;

public class Generate {
  public static void main(String[] args) throws Exception {
    String client = CodecCodeGen.assembleCodec("org.adamalang.net.codec", "ClientCodec", ClientMessage.class.getDeclaredClasses());
    String server = CodecCodeGen.assembleCodec("org.adamalang.net.codec", "ServerCodec", ServerMessage.class.getDeclaredClasses());
    Files.writeString(new File("./net/src/main/java/org/adamalang/net/codec/ClientCodec.java").toPath(), client);
    Files.writeString(new File("./net/src/main/java/org/adamalang/net/codec/ServerCodec.java").toPath(), server);
  }
}