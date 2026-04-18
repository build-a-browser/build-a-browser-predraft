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
    if (buffer.limit() < BUFFER_SIZE && buffer.hasArray()) {
      decoder.getInputBuffer().clear();
      decoder.getInputBuffer().put(buffer.array());
      decoder.push(buffer.limit());
      emitOutput();
    } else {
      while (buffer.position() < buffer.limit()) {
        decoder.getInputBuffer().clear();
        int toPush = Math.min(BUFFER_SIZE, buffer.remaining());
        ByteBuffer subBuffer = buffer.slice(buffer.position(), toPush);
        decoder.getInputBuffer().put(subBuffer);
        decoder.push(toPush);
        emitOutput();
        buffer.position(buffer.position() + toPush);
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
        onChunk.push(decoder.pull());
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
