package net.buildabrowser.babbrowser.network.encoding.decoders;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import com.aayushatharva.brotli4j.decoder.DecoderJNI.Status;

import net.buildabrowser.babbrowser.network.encoding.ContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry.ChunkHandler;

public class BrotliContentDecoder implements ContentDecoder {

  private static final int BUFFER_SIZE = 4096;

  static {
    Brotli4jLoader.ensureAvailability() ;
  }

  private final DecoderJNI.Wrapper decoder;
  private final ChunkHandler onChunk;

  public BrotliContentDecoder(ChunkHandler onChunk) throws IOException {
    this.decoder = new DecoderJNI.Wrapper(BUFFER_SIZE);
    this.onChunk = onChunk;
  }

  @Override
  public void push(ByteBuffer buffer) throws IOException {
    if (buffer.remaining() < BUFFER_SIZE) {
      int remaining = buffer.remaining();
      decoder.getInputBuffer().clear();
      decoder.getInputBuffer().put(buffer);
      decoder.getInputBuffer().flip();
      decoder.push(remaining);
      emitOutput();
    } else {
      while (buffer.remaining() > 0) {
        int toPush = Math.min(BUFFER_SIZE, buffer.remaining());
        ByteBuffer subBuffer = buffer.slice(buffer.position(), toPush);
        buffer.position(buffer.position() + toPush);
        decoder.getInputBuffer().clear();
        decoder.getInputBuffer().put(subBuffer);
        decoder.getInputBuffer().flip();
        decoder.push(toPush);
        emitOutput();
      }
    }
  }

  @Override
  public void done() throws IOException {
    if (decoder.getStatus().equals(Status.NEEDS_MORE_INPUT)) {
      decoder.push(0);
    }
    emitOutput();

    if (!decoder.getStatus().equals(Status.DONE)) {
      throw new IOException("Brotli stream finished early!");
    }
    onChunk.done();
  }

  private void emitOutput() throws IOException {
    while (
      decoder.getStatus().equals(Status.NEEDS_MORE_OUTPUT)
      || decoder.getStatus().equals(Status.OK)
    ) {
      if (decoder.getStatus().equals(Status.NEEDS_MORE_OUTPUT)) {
        // Yet another point the buffer needs copied. I guess zero-copy buffers just aren't a thing.
        ByteBuffer result = decoder.pull();
        ByteBuffer copy = ByteBuffer.allocate(result.limit()).put(result);
        copy.flip();
        onChunk.push(copy);
      } else {
        decoder.push(0);
      }
    }

    if (decoder.getStatus().equals(Status.ERROR)) {
      throw new IOException("Brotli decompression failed!");
    }
  }

  @Override
  public void close() throws IOException {
    decoder.destroy();
    onChunk.close();
  }

}
