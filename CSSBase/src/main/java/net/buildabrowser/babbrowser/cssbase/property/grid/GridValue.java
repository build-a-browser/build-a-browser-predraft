package net.buildabrowser.babbrowser.cssbase.property.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridValue(
  CSSValue rows,
  CSSValue columns,
  CSSValue areas,
  CSSValue autoRows,
  CSSValue autoColumns,
  CSSValue autoFlow
) implements CSSValue {
  
  public static GridValue create(
    CSSValue rows,
    CSSValue columns,
    CSSValue areas,
    CSSValue autoRows,
    CSSValue autoColumns,
    CSSValue autoFlow
  ) {
    return new GridValue(
      rows, columns, areas,
      autoRows, autoColumns, autoFlow);
  }

  @Override
  public String serialize() {
    return "<UNIMPLEMENTED>";
  }

}
