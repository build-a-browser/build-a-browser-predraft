package net.buildabrowser.babbrowser.network.encoding;

import java.io.IOException;

import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry.ChunkHandler;

public interface ContentDecoderFactory {
  
  ContentDecoder create(ChunkHandler onChunk) throws IOException;

}
