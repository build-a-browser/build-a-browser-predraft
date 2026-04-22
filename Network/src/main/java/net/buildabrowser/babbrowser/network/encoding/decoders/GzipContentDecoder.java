package net.buildabrowser.babbrowser.network.encoding.decoders;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry.ChunkHandler;

// Ported from my previous browser attempt, Webicity
public class GzipContentDecoder extends DeflateContentDecoder {

  private boolean headerProcessed = false;
  private long actualSize = 0;
  private CRC32 crc = new CRC32();

  public GzipContentDecoder(ChunkHandler onChunk) {
    super(onChunk, true);
  }

  @Override
  public void push(ByteBuffer buffer) throws IOException {
    if (!headerProcessed) {
      appendRemaining(buffer);
      if (
        !skipGZIPHeader(remainingData)
      ) return;

      headerProcessed = true;
      super.push(ByteBuffer.wrap(new byte[0]));
      return;
    }
    
    super.push(buffer);
  }

  @Override
  public void done() throws IOException {
    if (remainingData.limit() != 8) {
      throw new IOException("GZIP stream finished early!");
    }

    remainingData.order(ByteOrder.LITTLE_ENDIAN);
    long expectedCRC = Integer.toUnsignedLong(remainingData.getInt());
    long expectedSize = Integer.toUnsignedLong(remainingData.getInt());
    if (expectedCRC != crc.getValue()) {
      throw new IOException("GZIP integrity check failed: CRC Mismatch!");
    }
    if (expectedSize != actualSize) {
      throw new IOException("GZIP integrity check failed: Data was wrong size!");
    }

    super.done();
  }

  private boolean skipGZIPHeader(ByteBuffer buffer) {
    if (buffer.remaining() < 10) {
      return false;
    }

    if (buffer.get(0) != (byte) 0x1F || buffer.get(1) != (byte) 0x8B) {
      throw new IllegalArgumentException("Invalid GZIP header");
    }

    if (buffer.get(2) != (byte) 0x08) {
      throw new IllegalArgumentException("Unsupported compression method");
    }

    return skipExtraFields(buffer);
  }

  private boolean skipExtraFields(ByteBuffer buffer) {
    int offset = 10;
    byte headerTag = buffer.get(3);
    if ((headerTag & 0x04) != 0) { // FEXTRA
      if (buffer.remaining() < offset + 2) {
        return false;
      }
      offset += 2 + buffer.getShort(offset);
    }
    if ((headerTag & 0x08) != 0) { // FNAME
      offset = scanZeroTerminator(buffer, offset);
      if (offset == -1) return false;
    }
    if ((headerTag & 0x10) != 0) { // FCOMMENT
      offset = scanZeroTerminator(buffer, offset);
      if (offset == -1) return false;
    }
    if ((headerTag & 0x02) != 0) { // FHCRC
      if (buffer.remaining() < offset + 2) {
        return false;
      }
      offset += 2;
    }
    
    buffer.position(offset);

    return true;
  }

  private int scanZeroTerminator(ByteBuffer buffer, int offset) {
    for (int i = offset; i < buffer.remaining(); i++) {
      if (buffer.get(i) == 0) {
        return i + 1;
      }
    }
    return -1;
  }


  @Override
  protected void handleChunk(ByteBuffer chunk) throws IOException {
    int position = chunk.position();
    actualSize += chunk.remaining();
    crc.update(chunk);
    chunk.position(position);
    super.handleChunk(chunk);
  }
  
}
