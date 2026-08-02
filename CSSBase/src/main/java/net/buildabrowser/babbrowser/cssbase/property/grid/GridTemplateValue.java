package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridTemplateAreasRowValue;

public record GridTemplateValue(
  CSSValue rows, CSSValue columns, CSSValue areas
) implements CSSValue {

  public static final GridTemplateValue NONE = create(
    CSSValue.NONE, CSSValue.NONE, CSSValue.NONE);

  public record GridTemplateLineValue(
    List<String> startLines,
    GridTemplateAreasRowValue rowArea,
    CSSValue trackSize,
    List<String> endLines
  ) implements CSSValue {

    public static GridTemplateLineValue create(
      List<String> startLines,
      GridTemplateAreasRowValue rowArea,
      CSSValue trackSize,
      List<String> endLines
    ) {
      return new GridTemplateLineValue(
        startLines, rowArea, trackSize, endLines);
    }

  }
  
  public static GridTemplateValue create(
    CSSValue rows, CSSValue columns, CSSValue areas
  ) {
    return new GridTemplateValue(rows, columns, areas);
  }

}
