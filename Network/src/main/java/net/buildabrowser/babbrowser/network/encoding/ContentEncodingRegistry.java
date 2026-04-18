package net.buildabrowser.babbrowser.network.encoding;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

import net.buildabrowser.babbrowser.network.encoding.decoders.BrotliContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.decoders.DeflateContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.decoders.GzipContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.imp.ContentEncodingRegistryImp;

public interface ContentEncodingRegistry {
  
  void registerDecoder(String name, ContentDecoderFactory decoderFactory);

  ContentDecoder createDecoder(String name, ChunkHandler onChunk) throws IOException;

  ContentDecoder createChainDecoder(List<String> names, ChunkHandler onChunk) throws IOException;

  Set<String> acceptedEncodings();

  static ContentEncodingRegistry createEmpty() {
    return new ContentEncodingRegistryImp();
  }

  static ContentEncodingRegistry createDefault() {
    ContentEncodingRegistry registry = new ContentEncodingRegistryImp();
    registry.registerDecoder("gzip", GzipContentDecoder::new);
    registry.registerDecoder("deflate", DeflateContentDecoder::new);
    registry.registerDecoder("br", BrotliContentDecoder::new);
    return registry;
  }

  interface ChunkHandler extends Closeable {
    
    void push(ByteBuffer chunk) throws IOException;

    default void done() throws IOException {}

    default void close() throws IOException {}

  }

}
