package net.buildabrowser.babbrowser.cssbase.property.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridAutoFlowValue(
  GridAutoFlowDirection direction,
  boolean isDense
) implements CSSValue {
  
  public static enum GridAutoFlowDirection {
    ROW, COLUMN
  }

  public static GridAutoFlowValue create(
    GridAutoFlowDirection direction,
    boolean isDense
  ) {
    return new GridAutoFlowValue(direction, isDense);
  }

  @Override
  public String serialize() {
    return "<UNIMPLEMENTED>";
  }

}
