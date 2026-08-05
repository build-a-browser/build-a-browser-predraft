package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public final class GridItemPlacer {

  private static final LineNumberLookup COL_START_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_COLUMN_START,
    GridArea::x);
  private static final LineNumberLookup COL_END_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_COLUMN_END,
    area -> area.x() + area.w() - 1);
  private static final LineNumberLookup ROW_START_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_ROW_START,
    GridArea::y);
  private static final LineNumberLookup ROW_END_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_ROW_END,
    area -> area.y() + area.h() - 1);
  
  private GridItemPlacer() {}

  // List must be mutable
  public static void placeGridElements(
    Grid grid,
    ElementBox gridBox,
    List<GridItem> gridItemQueue
  ) {
    placeGridAreas(grid, gridBox);
    placeManualPositionedItems(grid, gridItemQueue);
  } 

  private static void placeGridAreas(Grid grid, ElementBox gridBox) {
    CSSValue gridTemplateAreasValue = gridBox.properties().get(CSSProperty.GRID_TEMPLATE_AREAS);
    if (gridTemplateAreasValue.equals(CSSValue.NONE)) return;
    List<GridArea> areas = ((GridTemplateAreasValue) gridTemplateAreasValue).areas();
    for (GridArea area: areas) {
      grid.addArea(area);
    }
  }

  private static void placeManualPositionedItems(
    Grid grid,
    List<GridItem> gridItemQueue
  ) {
    ListIterator<GridItem> queueIt = gridItemQueue.listIterator();
    while(queueIt.hasNext()) {
      GridItem item = queueIt.next();
      Integer colStart = lineNum(grid, item, COL_START_LOOKUP);
      Integer colEnd = lineNum(grid, item, COL_END_LOOKUP);
      Integer rowStart = lineNum(grid, item, ROW_START_LOOKUP);
      Integer rowEnd = lineNum(grid, item, ROW_END_LOOKUP);
      // TODO: Compute spans
      item.setSpan(colStart, colEnd, rowStart, rowEnd);

      if (
        colStart != null
        && colEnd != null
        && rowStart != null
        && rowEnd != null
      ) {
        System.out.println(colStart + " " + colEnd + " " + rowStart + " " + rowEnd);
        grid.placeItem(item, colStart, colEnd, rowStart, rowEnd);
        queueIt.remove();
      }
    }
  }

  // TODO: Having 4 methods is a bit redundant, merge them?

  private static Integer lineNum(
    Grid grid,
    GridItem item,
    LineNumberLookup lookup
  ) {
    ElementBox itemBox = item.itemBox();
    CSSValue maybeGridLineValue = itemBox.properties().get(lookup.relatedProperty());
    if (maybeGridLineValue.equals(CSSValue.AUTO)) return null;
    GridLineValue gridLineValue = (GridLineValue) maybeGridLineValue;
    if (gridLineValue.allowAreaName()) {
      GridArea gridArea = grid.area(gridLineValue.areaOrLineName());
      if (gridArea != null) {
        return lookup.gridAreaLine.apply(gridArea);
      }
    }

    return null; // TODO
  }

  private static record LineNumberLookup(
    CSSProperty relatedProperty,
    Function<GridArea, Integer> gridAreaLine
  ) {}

}
