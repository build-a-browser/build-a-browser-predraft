package net.buildabrowser.babbrowser.renderer.content.flow.mapping;

public class MappedStringBuilder {

  private final StringBuilder stringBuilder = new StringBuilder();
  private final MappingRLEBuffer rleBuffer = new MappingRLEBuffer();

  public void restart(String text) {
    stringBuilder.setLength(0);  
    stringBuilder.append(text);
    rleBuffer.pushRLE(0);
    rleBuffer.pushRLE(text.length());
  }

  public int length() {
    return stringBuilder.length();
  }

  public int codePointAt(int index) {
    return stringBuilder.codePointAt(index);
  }

  public void setCharAt(int index, char ch) {
    stringBuilder.setCharAt(index, ch);
  }

  public void delete(int start, int end) {
    stringBuilder.delete(start, end);
    rleBuffer.deleteRange(start, end);
  }

  public void deleteCharAt(int index) {
    stringBuilder.deleteCharAt(index);
    rleBuffer.deleteRange(index, index + 1);
  }

  public MappingRLEBuffer rleBuffer() {
    return rleBuffer;
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }

}