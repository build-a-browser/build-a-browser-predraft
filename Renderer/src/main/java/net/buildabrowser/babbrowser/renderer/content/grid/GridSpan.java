package net.buildabrowser.babbrowser.renderer.content.grid;

public record GridSpan(
  int colStart,
  int colEnd,
  int rowStart,
  int rowEnd
) {

  public static GridSpan create(
    int colStart,
    int colEnd,
    int rowStart,
    int rowEnd
  ) {
    return new GridSpan(colStart, colEnd, rowStart, rowEnd);
  }

  public int width() {
    return colEnd() - colStart() + 1;
  }

  public int height() {
    return rowEnd() - rowStart() + 1;
  }

  public int colLineStart() {
    return colStart();
  }

  public int colLineEnd() {
    return colEnd() + 1;
  }

  public int rowLineStart() {
    return rowStart();
  }

  public int rowLineEnd() {
    return rowEnd() + 1;
  }
  
}
