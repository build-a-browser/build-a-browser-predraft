package net.buildabrowser.babbrowser.renderer.content.grid;

public record GridSpan(
  int rowStart,
  int rowEnd,
  int colStart,
  int colEnd
) {

  public static GridSpan create(
    int rowStart,
    int rowEnd,
    int colStart,
    int colEnd
  ) {
    return new GridSpan(rowStart, rowEnd, colStart, colEnd);
  }
  
}
