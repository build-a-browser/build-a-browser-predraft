package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridTemplateAreasValue(List<CSSValue> rows) implements CSSValue {
  
  public static CSSValue create(List<CSSValue> rows) {
    return new GridTemplateAreasValue(rows);
  }

  public static record GridTemplateAreasRowValue(
    List<String> cellNames
  ) implements CSSValue {

    public static CSSValue create(List<String> cellNames) {
      return new GridTemplateAreasRowValue(cellNames);
    }
  
  }

}
