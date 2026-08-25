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

  public int size(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> width();
      case ROW -> height();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  public int colLineStart() {
    return colStart();
  }

  public int colLineEnd() {
    return colEnd() + 1;
  }

  public int trackStart(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> colStart();
      case ROW -> rowStart();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  public int trackEnd(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> colEnd();
      case ROW -> rowEnd();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  public int rowLineStart() {
    return rowStart();
  }

  public int rowLineEnd() {
    return rowEnd() + 1;
  }

  public int lineStart(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> colLineStart();
      case ROW -> rowLineStart();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  public int lineEnd(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> colLineEnd();
      case ROW -> rowLineEnd();
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  public GridSpan withDimension(
    GridDirection direction,
    int minLine,
    int maxLine
  ) {
    maxLine = Math.max(maxLine, minLine);
    return switch (direction) {
      case COLUMN -> GridSpan.create(minLine, maxLine, rowStart(), rowEnd());
      case ROW -> GridSpan.create(colStart(), colEnd(), minLine, maxLine);
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }
  
}
