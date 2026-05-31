package net.buildabrowser.babbrowser.network.encoding.decoders;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.slice;

import java.io.IOException;
import java.nio.Buffer;
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
    ByteBuffer decoderByteBuffer = decoder.getInputBuffer();
    Buffer decoderBuffer = decoderByteBuffer;
    if (buffer.remaining() < BUFFER_SIZE) {
      int remaining = buffer.remaining();
      decoderBuffer.clear();
      decoderByteBuffer.put(buffer);
      decoderBuffer.flip();
      decoder.push(remaining);
      emitOutput();
    } else {
      while (buffer.remaining() > 0) {
        int toPush = Math.min(BUFFER_SIZE, buffer.remaining());
        ByteBuffer subBuffer = slice(buffer, buffer.position(), toPush);
        ((Buffer) buffer).position(buffer.position() + toPush);
        decoderBuffer.clear();
        decoderByteBuffer.put(subBuffer);
        decoderBuffer.flip();
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
        ((Buffer) copy).flip();
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
