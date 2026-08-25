package net.buildabrowser.babbrowser.renderer.content.grid;

public enum GridDirection {
  
  ROW, COLUMN;

  public GridDirection rotate() {
    return switch (this) {
      case ROW -> COLUMN;
      case COLUMN -> ROW;
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + this);
    };
  }

}
