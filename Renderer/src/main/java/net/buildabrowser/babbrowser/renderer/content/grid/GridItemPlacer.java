package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public final class GridItemPlacer {

  private static final LineNumberLookup COL_START_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_COLUMN_START, "-start",
    GridArea::x, GridSpan::colLineStart, GridSpan::colLineEnd, Grid::columnLine);
  private static final LineNumberLookup COL_END_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_COLUMN_END, "-end",
    area -> area.x() + area.w(), GridSpan::colLineStart, GridSpan::colLineEnd, Grid::columnLine);
  private static final LineNumberLookup ROW_START_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_ROW_START, "-start",
    GridArea::y, GridSpan::rowLineStart, GridSpan::rowLineEnd, Grid::rowLine);
  private static final LineNumberLookup ROW_END_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_ROW_END, "-end",
  area -> area.y() + area.h(), GridSpan::rowLineStart, GridSpan::rowLineEnd, Grid::rowLine);
  
  private GridItemPlacer() {}

  // List must be mutable
  public static void placeGridElements(
    Grid grid,
    ElementBox gridBox,
    List<GridItem> gridItemQueue
  ) {
    placeGridAreas(grid, gridBox);
    determineManualItemPositions(grid, gridItemQueue);
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

  private static void determineManualItemPositions(
    Grid grid,
    List<GridItem> gridItemQueue
  ) {
    for (GridItem item: gridItemQueue) {
      LineNumberPair colLines = lineNumPair(
        grid, item, COL_START_LOOKUP, COL_END_LOOKUP);
      LineNumberPair rowLines = lineNumPair(
        grid, item, ROW_START_LOOKUP, ROW_END_LOOKUP);
      // TODO: Compute spans
      item.setSpan(
        colLines.lineNumStart(), colLines.lineNumEnd(),
        rowLines.lineNumStart(), rowLines.lineNumEnd());
    }
  }

  private static void placeManualPositionedItems(
    Grid grid,
    List<GridItem> gridItemQueue
  ) {
    ListIterator<GridItem> queueIt = gridItemQueue.listIterator();
    while (queueIt.hasNext()) {
      GridItem item = queueIt.next();
      if (
        item.colStart() != null
        && item.colEnd() != null
        && item.rowStart() != null
        && item.rowEnd() != null
      ) {
        grid.placeItem(
          item,
          item.colStart(), item.colEnd(),
          item.rowStart(), item.rowEnd());
        queueIt.remove();
      }
    }
  }

  // TODO: Allocating a record just to return is not great.
  // Does Java optimize this to live on the stack?
  private static LineNumberPair lineNumPair(
    Grid grid,
    GridItem item,
    LineNumberLookup lookup1,
    LineNumberLookup lookup2
  ) {
    ElementBox itemBox = item.itemBox();
    CSSValue maybeGridLineValueStart = itemBox.properties().get(lookup1.relatedProperty());
    CSSValue maybeGridLineValueEnd = itemBox.properties().get(lookup2.relatedProperty());
    Integer dimStart = definiteLineNum(grid, maybeGridLineValueStart, lookup1);
    Integer dimEnd = definiteLineNum(grid, maybeGridLineValueEnd, lookup2);
    if (dimStart == null && dimEnd == null) {
      return new LineNumberPair(dimEnd, dimStart);
    } else if (dimStart != null && dimEnd != null) {
      return
        dimStart > dimEnd ? new LineNumberPair(dimEnd, dimStart) :
        dimStart == dimEnd ? new LineNumberPair(dimStart, dimEnd + 1) :
        new LineNumberPair(dimStart, dimEnd);
    }

    if (
      maybeGridLineValueEnd instanceof GridLineValue gridLineValue
      && gridLineValue.isSpan()
    ) {
      dimEnd = trackPos(
        grid, lookup1,
        gridLineValue.areaOrLineName(),
        dimStart + 1,
        gridLineValue.lineNumber());
    } else if (
      maybeGridLineValueStart instanceof GridLineValue gridLineValue
      && gridLineValue.isSpan()
    ) {
      dimStart = trackPos(
        grid, lookup1,
        gridLineValue.areaOrLineName(),
        dimStart - 1,
        -gridLineValue.lineNumber());
    }

    dimStart = dimStart == null ? dimEnd - 1 : dimStart;
    dimEnd = dimEnd == null ? dimStart + 1 : dimEnd;

    return new LineNumberPair(dimStart, dimEnd);
  }

  private static Integer definiteLineNum(
    Grid grid,
    CSSValue maybeGridLineValue,
    LineNumberLookup lookup
  ) {
    if (maybeGridLineValue.equals(CSSValue.AUTO)) return null;
    GridLineValue gridLineValue = (GridLineValue) maybeGridLineValue;
    if (gridLineValue.allowAreaName()) {
      String searchName = gridLineValue.areaOrLineName() + lookup.areaVariant();
      int searchStart = lookup.gridLineStartFunc.apply(grid.explicitSpan());
      int areaLinePos = trackPos(
        grid, lookup,
        searchName,
        searchStart,
        1);
      if (isExplicitLine(grid, lookup, areaLinePos)) {
        return areaLinePos;
      }
    }

    if (gridLineValue.isSpan()) {
      return null;
    }

    int searchStart = gridLineValue.lineNumber() > 0 ?
      lookup.gridLineStartFunc.apply(grid.explicitSpan()) :
      lookup.gridLineEndFunc.apply(grid.explicitSpan());
    return trackPos(
      grid, lookup,
      gridLineValue.areaOrLineName(),
      searchStart,
      gridLineValue.lineNumber());
  }

  private static int trackPos(
    Grid grid,
    LineNumberLookup lookup,
    String areaOrLineName,
    int searchPos,
    int lineNumber
  ) {
    int remaining = lineNumber > 0 ?
      lineNumber : -lineNumber;
    int direction = lineNumber > 0 ? 1 : -1;
    remaining--;

    boolean isLineName = false;
    while (
      isExplicitLine(grid, lookup, searchPos)
      && (
        !(isLineName = isLineName(grid, lookup, areaOrLineName, searchPos))
        || remaining > 0)
    ) {
      searchPos += direction;
      if (isLineName) remaining--;
    }

    int sideRemaining = lineNumber > 0 ?
      remaining : -remaining;
    return searchPos + sideRemaining;
  }

  private static boolean isLineName(
    Grid grid,
    LineNumberLookup lookup,
    String areaOrLineName,
    int searchPos
  ) {
    if (areaOrLineName == null) return true;
    if (!isExplicitLine(grid, lookup, searchPos)) return true;
    return lookup.lineGetter.apply(grid, searchPos).hasName(areaOrLineName);
  }

  private static boolean isExplicitLine(
    Grid grid,
    LineNumberLookup lookup,
    int searchPos
  ) {
    return
      searchPos >= lookup.gridLineStartFunc().apply(grid.explicitSpan())
      && searchPos <= lookup.gridLineEndFunc().apply(grid.explicitSpan());
  }

  private static record LineNumberPair(
    Integer lineNumStart, Integer lineNumEnd
  ) {}

  private static record LineNumberLookup(
    CSSProperty relatedProperty,
    String areaVariant,
    Function<GridArea, Integer> gridAreaLine,
    Function<GridSpan, Integer> gridLineStartFunc,
    Function<GridSpan, Integer> gridLineEndFunc,
    BiFunction<Grid, Integer, GridLine> lineGetter
  ) {}

}
