package net.buildabrowser.babbrowser.renderer.content.flow.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MappingRLEBufferTest {

  private final short[] sampleRleList = new short[] {
    s(20), s(80), s(50), s(80)
  };
  
  private MappingRLEBuffer buffer;

  @BeforeEach
  public void beforeEach() {
    this.buffer = new MappingRLEBuffer();
  }

  @Test
  @DisplayName("Can push RLE values")
  public void canPushRLEValues() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    Assertions.assertArrayEquals(
      new short[] { s(0), s(80) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can increase RLE values")
  public void canIncreaseRLEValues() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.increaseRLE(20);
    Assertions.assertArrayEquals(
      new short[] { s(0), s(100) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can merge contiguous RLE values")
  public void canMergeContiguousRLEValues() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    Assertions.assertArrayEquals(
      new short[] { s(0), s(160) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can not merge non-contiguous RLE values")
  public void canNotMergeNonContiguousRLEValues() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(1);
    buffer.pushRLE(80);
    Assertions.assertArrayEquals(
      new short[] { s(0), s(80), s(1), s(80) },
      buffer.toShortArray());
  }

  //

  @Test
  @DisplayName("Can match source for equal start and length")
  public void canMatchSourceForEqualStartAndLength() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);

    Assertions.assertTrue(buffer.matchesSource(80));
  }

  @Test
  @DisplayName("Can not match source for equal start but differing length")
  public void canNotMatchSourceForEqualStartButDifferingLength() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);

    Assertions.assertFalse(buffer.matchesSource(79));
  }

  @Test
  @DisplayName("Can not match source for equal length but differing start")
  public void canNotMatchSourceForEqualLengthButDifferingStart() {
    buffer.pushRLE(1);
    buffer.pushRLE(80);

    Assertions.assertFalse(buffer.matchesSource(80));
  }

  @Test
  @DisplayName("Can not match source for no items")
  public void canNotMatchSourceForNoItems() {
    Assertions.assertFalse(buffer.matchesSource(80));
  }

  @Test
  @DisplayName("Can not match source for too many items")
  public void canNotMatchSourceForTooManyItems() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(0);
    buffer.pushRLE(80);

    Assertions.assertFalse(buffer.matchesSource(80));
  }

  // Does not support merging sources unless converting to short[], so don't test total length for matchSource

  //

  @Test
  @DisplayName("Can delete empty range")
  public void canDeleteEmptyRange() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.deleteRange(0, 0);

    // Collapses
    Assertions.assertArrayEquals(
      new short[] { s(0), s(160) },
      buffer.toShortArray());
  }
  
  @Test
  @DisplayName("Can delete range equal to one component")
  public void canDeleteRangeEqualToOneComponent() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.deleteRange(0, 80);

    Assertions.assertArrayEquals(
      new short[] { s(80), s(80) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can delete range within one component")
  public void canDeleteRangeWithinOneComponent() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.deleteRange(20, 40);

    Assertions.assertArrayEquals(
      new short[] { s(0), s(20), s(20), s(120) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can delete range spanning two components")
  public void canDeleteRangeSpanningTwoComponents() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.deleteRange(20, 140);

    Assertions.assertArrayEquals(
      new short[] { s(0), s(20), s(120), s(20) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can delete range spanning two components with gap")
  public void canDeleteRangeSpanningTwoComponentsWithGap() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(50);
    buffer.pushRLE(80);
    buffer.deleteRange(20, 140);

    Assertions.assertArrayEquals(
      new short[] { s(0), s(20), s(170), s(20) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can delete range equaling total buffer")
  public void canDeleteRangeEqualingTotalBuffer() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(50);
    buffer.pushRLE(80);
    buffer.deleteRange(20, 160);

    // TODO: Currently the tail is not collapsed. Do so in the future?
    Assertions.assertArrayEquals(
      new short[] { s(0), s(20), s(190), s(0) },
      buffer.toShortArray());
  }

  @Test
  @DisplayName("Can not delete range overaging total buffer")
  public void canNotDeleteRangeOveragingTotalBuffer() {
    buffer.pushRLE(0);
    buffer.pushRLE(80);
    buffer.pushRLE(50);
    buffer.pushRLE(80);
    Assertions.assertThrows(IllegalStateException.class,
      () -> buffer.deleteRange(20, 1000));
  }

  //

  @Test
  @DisplayName("Can get source index for zero")
  public void canGetSourceIndexForZero() {
    int actual = MappingRLEBuffer.sourceIndex(0, sampleRleList);
    Assertions.assertEquals(20, actual);
  }

  @Test
  @DisplayName("Can get source index for one")
  public void canGetSourceIndexForOne() {
    int actual = MappingRLEBuffer.sourceIndex(1, sampleRleList);
    Assertions.assertEquals(21, actual);
  }

  @Test
  @DisplayName("Can get source index for end of chunk")
  public void canGetSourceIndexForEndOfChunk() {
    int actual = MappingRLEBuffer.sourceIndex(80, sampleRleList);
    Assertions.assertEquals(100, actual);
  }

  @Test
  @DisplayName("Can get source index for start of next chunk")
  public void canGetSourceIndexForStartOfNextChunk() {
    int actual = MappingRLEBuffer.sourceIndex(81, sampleRleList);
    Assertions.assertEquals(151, actual);
  }

  @Test
  @DisplayName("Can get source index for end of chunks")
  public void canGetSourceIndexForEndOfChunks() {
    int actual = MappingRLEBuffer.sourceIndex(160, sampleRleList);
    Assertions.assertEquals(230, actual);
  }

  //
  // Text index tests are mostly the opposite of source index tests

  @Test
  @DisplayName("Can get text index for zero")
  public void canGetTextIndexForZero() {
    int actual = MappingRLEBuffer.textIndex(0, sampleRleList);
    Assertions.assertEquals(0, actual);
  }

  @Test
  @DisplayName("Can get text index for text start")
  public void canGetTextIndexForTextStart() {
    int actual = MappingRLEBuffer.textIndex(20, sampleRleList);
    Assertions.assertEquals(0, actual);
  }

  @Test
  @DisplayName("Can get text index one")
  public void canGetTextIndexOne() {
    int actual = MappingRLEBuffer.textIndex(21, sampleRleList);
    Assertions.assertEquals(1, actual);
  }

  @Test
  @DisplayName("Can get text index for end of chunk")
  public void canGetTextIndexForEndOfChunk() {
    int actual = MappingRLEBuffer.textIndex(100, sampleRleList);
    Assertions.assertEquals(80, actual);
  }

  @Test
  @DisplayName("Can get text index for start of next chunk")
  public void canGetTextIndexForStartOfNextChunk() {
    int actual = MappingRLEBuffer.textIndex(151, sampleRleList);
    Assertions.assertEquals(81, actual);
  }

  @Test
  @DisplayName("Can get text index for end of chunks")
  public void canGetTextIndexForEndOfChunks() {
    int actual = MappingRLEBuffer.textIndex(230, sampleRleList);
    Assertions.assertEquals(160, actual);
  }

  private short s(int i) {
    return (short) i;
  }

}
