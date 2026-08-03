package net.buildabrowser.babbrowser.renderer.content.flow.mapping;

public class MappingRLEBuffer implements Cloneable {

  private int[] rleList = new int[2];
  private int rleSize = 0;

  public MappingRLEBuffer() {}

  public MappingRLEBuffer(
    int[] rleList, int rleSize
  ) {
    this.rleList = rleList;
    this.rleSize = rleSize;
  }

  public void pushRLE(int value) {
    resizeIfNeeded(rleSize + 1);
    rleList[rleSize++] = value;
  }

  public void increaseRLE(int amount) {
    rleList[rleSize - 1] += amount;
  }

  public boolean matchesSource(int sourceLength) {
    return
      rleSize == 2
      && rleList[0] == 0
      && rleList[1] == sourceLength;
  }

  public void deleteRange(int startIndex, int endIndex) {
    int range = endIndex - startIndex;
    if (range == 0) return;

    int[] textStartIndex = new int[1];
    int[] textEndIndex = new int[1];
    int startRleIndex = textRleIndex(startIndex, textStartIndex);
    int endRleIndex = textRleIndex(endIndex, textEndIndex);
    if (
      startRleIndex == endRleIndex
      && rleList[startRleIndex] == range
    ) {
      assert startRleIndex >= 1;
      rleList[startRleIndex] = 0;
      rleList[startRleIndex - 1] += range;
    } else if (startRleIndex == endRleIndex) {
      int lenBefore = startIndex - textStartIndex[0];
      int lenAfter = rleList[startRleIndex] - lenBefore - range;

      resizeIfNeeded(rleSize + 2);

      System.arraycopy(
        rleList, startRleIndex + 1,
        rleList, startRleIndex + 3,
        rleSize - (startRleIndex + 1));

      rleList[startRleIndex] = lenBefore;
      rleList[startRleIndex + 1] = range;
      rleList[startRleIndex + 2] = lenAfter;

      rleSize += 2;
    } else {
      int collapsedRleIndex = startRleIndex + 1;

      int totalCollapsed = range;
      for (int rlePos = startRleIndex + 1; rlePos < endRleIndex; rlePos++) {
        if (rlePos % 2 == 0) {
          totalCollapsed += rleList[rlePos];
        }
        rleList[rlePos] = 0;
      }
      rleList[collapsedRleIndex] = totalCollapsed;

      rleList[startRleIndex] = startIndex - textStartIndex[0];
      rleList[endRleIndex] = rleList[endRleIndex] - (endIndex - textEndIndex[0]);
    }
  }

  @Override
  public MappingRLEBuffer clone() {
    int[] newArr = new int[rleList.length];
    System.arraycopy(rleList, 0, newArr, 0, rleSize);
    return new MappingRLEBuffer(newArr, rleSize);
  }

  public short[] toShortArray() {
    short[] newList = new short[sizeAsShorts()];
    int listIndex = 0;
    
    int maxValue = Short.toUnsignedInt((short) -1);
    for (int i = 0; i < rleSize; i++) {
      int value = rleList[i];
      while (
        i + 2 < rleSize
        && rleList[i + 1] == 0
      ) {
        i += 2;
        value += rleList[i];
      }

      while (value >= maxValue) {
        value -= maxValue;
        newList[listIndex++] = (short) -1;
      }
      newList[listIndex++] = (short) value;
    }

    return newList;
  }

  private int sizeAsShorts() {
    int sizeAsShorts = 0;
    int maxValue = Short.toUnsignedInt((short) -1);
    for (int i = 0; i < rleSize; i++) {
      int value = rleList[i];
      while (
        i + 2 < rleSize
        && rleList[i + 1] == 0
      ) {
        i += 2;
        value += rleList[i];
      }

      while (value >= maxValue) {
        value -= maxValue;
        sizeAsShorts++;
      }
      sizeAsShorts++;
    }

    return sizeAsShorts;
  }

  private void resizeIfNeeded(int desiredSize) {
    int newSize = rleList.length;
    while (newSize < desiredSize) {
      newSize *= 2;
    }

    if (newSize != rleSize) {
      int[] newList = new int[newSize];
      System.arraycopy(rleList, 0, newList, 0, rleList.length);
      this.rleList = newList;
    }
  }

  private int textRleIndex(int index, int[] rleTextIndex) {
    int rlePos;
    for (rlePos = 0; rlePos < rleSize; rlePos++) {
      if (rlePos % 2 == 0) continue;

      int rleLen = rleList[rlePos];

      if (
        rleTextIndex[0] + rleLen >= index
      ) return rlePos;

      rleTextIndex[0] += rleLen;
    }

    throw new IllegalStateException("No mapping for text index!");
  }

  //

  public static int sourceIndex(int textIndex, short[] rleList) {
    int sourceIndex = 0;
    int currentTextIndex = 0;
    boolean isCollapseMode = true;
    int rlePointer = 0;
    while (
      rlePointer < rleList.length &&
      (currentTextIndex < textIndex || isCollapseMode)
    ) {
      short rleRaw = rleList[rlePointer];
      int rleLen = Short.toUnsignedInt(rleRaw);
      boolean isModeChange = rleRaw != -1;
      rlePointer++;

      if (isCollapseMode) {
        sourceIndex += rleLen;
      } else {
        if (currentTextIndex + rleLen >= textIndex) {
          return sourceIndex + (textIndex - currentTextIndex);
        }

        sourceIndex += rleLen;
        currentTextIndex += rleLen;
      }

      if (isModeChange) {
        isCollapseMode = !isCollapseMode;
      }
    }

    if (textIndex <= currentTextIndex) {
      return sourceIndex;
    } else {
      throw new IllegalStateException("No mapping for textIndex!");
    }
  }

  public static int textIndex(int sourceIndex, short[] rleList) {
    int textIndex = 0;
    int currentSourceIndex = 0;
    boolean isCollapseMode = true;
    int rlePointer = 0;
    while (
      currentSourceIndex < sourceIndex
      && rlePointer < rleList.length
    ) {
      short rleRaw = rleList[rlePointer];
      int rleLen = Short.toUnsignedInt(rleRaw);
      boolean isModeChange = rleLen != -1;
      rlePointer++;

      if (isCollapseMode) {
        currentSourceIndex += rleLen;
      } else {
        if (currentSourceIndex + rleLen >= sourceIndex) {
          return textIndex + (sourceIndex - currentSourceIndex);
        }

        currentSourceIndex += rleLen;
        textIndex += rleLen;
      }

      if (isModeChange) {
        isCollapseMode = !isCollapseMode;
      }
    }

    if (sourceIndex <= currentSourceIndex) {
      return textIndex;
    } else {
      throw new IllegalStateException("No mapping for sourceIndex!");
    }
  }
  
}
