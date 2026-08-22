package net.buildabrowser.babbrowser.debugger.core;

public record DebugSideDimensions(
  float top, float bottom, float left, float right
) {
  
  public String serialize() {
    return String.format(
      "top: %spx, bottom: %spx, left: %spx, right: %spx",
      top, bottom, left, right);
  }

}
