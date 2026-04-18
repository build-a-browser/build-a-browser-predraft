package net.buildabrowser.babbrowser.network.encoding.decoders;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.buildabrowser.babbrowser.network.encoding.ContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry.ChunkHandler;

public class IdentityContentDecoder implements ContentDecoder {

  private final ChunkHandler onChunk;

  public IdentityContentDecoder(ChunkHandler onChunk) {
    this.onChunk = onChunk;
  }

  @Override
  public void push(ByteBuffer buffer) throws IOException {
    onChunk.push(buffer);
  }

  @Override
  public void done() throws IOException {
    onChunk.done();
  }

  @Override
  public void close() throws IOException {
    onChunk.close();
  }
  
}
