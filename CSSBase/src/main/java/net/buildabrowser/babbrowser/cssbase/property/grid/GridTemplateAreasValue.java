package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridTemplateAreasValue(List<GridArea> areas) implements CSSValue {
  
  public static CSSValue create(List<GridArea> areas) {
    return new GridTemplateAreasValue(areas);
  }

  public static record GridTemplateAreasRowValue(
    List<String> cellNames
  ) implements CSSValue {

    public static GridTemplateAreasRowValue create(List<String> cellNames) {
      return new GridTemplateAreasRowValue(cellNames);
    }
  
  }

  public static record GridArea(
    String name, int x, int y, int w, int h
  ) {

    public static GridArea create(
      String name, int x, int y, int w, int h
    ) {
      return new GridArea(name, x, y, w, h);
    }

  }

}
