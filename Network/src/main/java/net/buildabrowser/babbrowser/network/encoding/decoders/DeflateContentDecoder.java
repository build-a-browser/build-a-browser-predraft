package net.buildabrowser.babbrowser.network.encoding.decoders;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import net.buildabrowser.babbrowser.network.encoding.ContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry.ChunkHandler;

// Ported from my previous browser attempt, Webicity
public class DeflateContentDecoder implements ContentDecoder {
  
  private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);

  private final Inflater inflater;
  private final ChunkHandler onChunk;

  protected ByteBuffer remainingData = EMPTY_BUFFER;

  public DeflateContentDecoder(ChunkHandler onChunk) {
    this(onChunk, false);
  }

  public DeflateContentDecoder(ChunkHandler onChunk, boolean noWrap) {
    this.onChunk = onChunk;
    this.inflater = new Inflater(noWrap);
  }

  @Override
  public void push(ByteBuffer buffer) throws IOException {
    try {
      appendRemaining(buffer);
      buffer = remainingData;
      remainingData = EMPTY_BUFFER;
      
      inflater.setInput(buffer);
      
      while (!(inflater.finished() || inflater.needsInput())) {
        byte[] buf = new byte[1024];
        int count = inflater.inflate(buf);
        ByteBuffer chunk = ByteBuffer.wrap(buf, 0, count);
        handleChunk(chunk);
      }

      if (inflater.needsDictionary()) {
        throw new RuntimeException("Dictionary needed during decompression");
      }

      if (inflater.getRemaining() > 0) {
        remainingData = ByteBuffer.allocate(inflater.getRemaining());
        remainingData.put(buffer.array(), buffer.limit() - inflater.getRemaining(), inflater.getRemaining());
        remainingData.flip();
      }
    } catch (DataFormatException e) {
      throw new IOException("Data format error during decompression", e);
    }
  }

  @Override
  public void done() throws IOException {
    if (!inflater.finished() || remainingData.position() < remainingData.limit()) {
      throw new IOException("Inflate stream finished early!");
    }
    onChunk.done();
  }

  @Override
  public void close() throws IOException {
    inflater.end();
    onChunk.close();
  }

  protected void handleChunk(ByteBuffer chunk) throws IOException {
    onChunk.push(chunk);
  }

  protected void appendRemaining(ByteBuffer buffer) {
    ByteBuffer newBuffer = ByteBuffer.allocate(remainingData.remaining() + buffer.remaining());
    newBuffer.put(remainingData);
    newBuffer.put(buffer);
    newBuffer.flip();
    remainingData = newBuffer;
  }

}
