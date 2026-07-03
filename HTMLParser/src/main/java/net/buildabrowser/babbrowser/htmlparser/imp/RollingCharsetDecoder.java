package net.buildabrowser.babbrowser.htmlparser.imp;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class RollingCharsetDecoder {

  private final ByteBuffer leftoverBuffer = ByteBuffer.allocate(16);

  private final CharsetDecoder innerDecoder;

  public RollingCharsetDecoder(CharsetDecoder innerDecoder) {
    this.innerDecoder = innerDecoder;
  }

  public void decode(ByteBuffer in, CharBuffer out, Runnable onChunkAvailable) {
    if (leftoverBuffer.position() != 0) {
      boolean didProcessBytes = handleLeftoverBuffer(in, out, onChunkAvailable);
      if (!didProcessBytes) return;
    }


    ((Buffer) leftoverBuffer).clear();
    CoderResult lastResult = CoderResult.OVERFLOW;
    while (lastResult.equals(CoderResult.OVERFLOW)) {
      lastResult = innerDecoder.decode(in, out, false);
      ((Buffer) out).flip();
      onChunkAvailable.run();
      ((Buffer) out).clear();
    }

    if (lastResult.equals(CoderResult.UNDERFLOW)) {
      leftoverBuffer.put(in);
    }
  }

  private boolean handleLeftoverBuffer(ByteBuffer in, CharBuffer out, Runnable onChunkAvailable) {
    int oldInPosition = in.position();
    int oldInLimit = in.limit();
    int oldLeftoverPosition = leftoverBuffer.position();
    int bytesToCopy = leftoverBuffer.remaining();
    ((Buffer) in).limit(in.position() + bytesToCopy);
    leftoverBuffer.put(in);
    ((Buffer) leftoverBuffer).flip();
    innerDecoder.decode(leftoverBuffer, out, false);
    int positionInc = Math.max(0, leftoverBuffer.position() - oldLeftoverPosition);
    leftoverBuffer.compact();
    ((Buffer) in).limit(oldInLimit);
    ((Buffer) in).position(oldInPosition + positionInc);

    ((Buffer) out).flip();
    onChunkAvailable.run();
    ((Buffer) out).clear();

    return positionInc > 0;
  }

  public void flush(CharBuffer out, Runnable onChunkAvailable) {
    CoderResult lastResult = CoderResult.OVERFLOW;
    while (lastResult.equals(CoderResult.OVERFLOW)) {
      lastResult = innerDecoder.decode(leftoverBuffer, out, true);
      ((Buffer) out).flip();
      onChunkAvailable.run();
      ((Buffer) out).clear();
    }

    lastResult = CoderResult.OVERFLOW;
    while (lastResult.equals(CoderResult.OVERFLOW)) {
      lastResult = innerDecoder.flush(out);
      ((Buffer) out).flip();
      onChunkAvailable.run();
      ((Buffer) out).clear();
    }
  }
  
}