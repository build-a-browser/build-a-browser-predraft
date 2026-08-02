package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridTemplateAreasValue(List<GridTemplateAreasRowValue> rows) implements CSSValue {
  
  public static CSSValue create(List<GridTemplateAreasRowValue> rows) {
    return new GridTemplateAreasValue(rows);
  }

  public static record GridTemplateAreasRowValue(
    List<String> cellNames
  ) implements CSSValue {

    public static GridTemplateAreasRowValue create(List<String> cellNames) {
      return new GridTemplateAreasRowValue(cellNames);
    }
  
  }

}
