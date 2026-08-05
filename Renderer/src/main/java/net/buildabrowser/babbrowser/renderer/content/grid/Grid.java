package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridImp;

public interface Grid {

  GridSpan explicitSpan();

  GridSpan implicitSpan();

  void resizeExplicit(GridSpan span);

  void resizeImplicit(GridSpan span);
  
  void placeRowLineName(
    String lineName,
    int rowNum
  );

  void placeColumnLineName(
    String lineName,
    int colNum
  );

  int linePos(String name, int index);

  void addArea(GridArea area);

  GridArea area(String areaName);

  void placeItem(
    GridItem item,
    int colStart,
    int colEnd,
    int rowStart,
    int rowEnd
  );

  GridItem cell(int x, int y, int z);

  static Grid create() {
    return new GridImp();
  }

}
