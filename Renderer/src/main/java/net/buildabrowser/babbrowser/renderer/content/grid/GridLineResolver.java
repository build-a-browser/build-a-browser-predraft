package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.function.Function;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public class GridLineResolver {

  private static final LineNumberLookup COL_START_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_COLUMN_START, "-start", GridDirection.COLUMN,
    GridArea::x);
  private static final LineNumberLookup COL_END_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_COLUMN_END, "-end", GridDirection.COLUMN,
    area -> area.x() + area.w());
  private static final LineNumberLookup ROW_START_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_ROW_START, "-start", GridDirection.ROW,
    GridArea::y);
  private static final LineNumberLookup ROW_END_LOOKUP = new LineNumberLookup(
    CSSProperty.GRID_ROW_END, "-end", GridDirection.ROW,
    area -> area.y() + area.h());

  public static LineNumberPair itemColumnLines(Grid grid, GridItem item) {
    return lineNumPair(
      grid, item, COL_START_LOOKUP, COL_END_LOOKUP);
  }

  public static LineNumberPair itemRowLines(Grid grid, GridItem item) {
    return lineNumPair(
      grid, item, ROW_START_LOOKUP, ROW_END_LOOKUP);
  }

  public static Integer itemAutoEnd(
    Grid grid,
    GridItem item,
    GridDirection direction,
    int dirStart
  ) {
    CSSValue spanValue = itemSpanValue(item, direction);
    
    Integer dirEnd = item.lineEnd(direction) != null ?
      item.lineEnd(direction) :
      spanValue instanceof GridLineValue lineValue && lineValue.isSpan() ?
        trackPos(
          grid, direction,
          lineValue.areaOrLineName(),
          dirStart + 1,
          lineValue.lineNumber()) :
        dirStart + 1;
    
    return dirEnd == null || dirEnd <= dirStart ?
      dirStart + 1 :
      dirEnd;
  }

  public static CSSValue itemSpanValue(GridItem item, GridDirection direction) {
    ElementBox itemBox = item.itemBox();
    CSSValue spanValue = direction.equals(GridDirection.ROW) ?
      itemBox.properties().get(CSSProperty.GRID_ROW_START) :
      itemBox.properties().get(CSSProperty.GRID_COLUMN_START);
    if (spanValue.equals(CSSValue.AUTO)) {
      spanValue = direction.equals(GridDirection.ROW) ?
        itemBox.properties().get(CSSProperty.GRID_ROW_END) :
        itemBox.properties().get(CSSProperty.GRID_COLUMN_END);
    }

    // If either side were definite, it would have been resolved earlier
    assert
      spanValue.equals(CSSValue.AUTO)
      || (spanValue instanceof GridLineValue lineValue && lineValue.isSpan());

    return spanValue;
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
      maybeGridLineValueEnd instanceof GridLineValue lineValue
      && lineValue.isSpan()
    ) {
      dimEnd = trackPos(
        grid, lookup1.direction(),
        lineValue.areaOrLineName(),
        dimStart + 1,
        lineValue.lineNumber());
    } else if (
      maybeGridLineValueStart instanceof GridLineValue lineValue
      && lineValue.isSpan()
    ) {
      dimStart = trackPos(
        grid, lookup1.direction(),
        lineValue.areaOrLineName(),
        dimStart - 1,
        -lineValue.lineNumber());
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
    GridDirection direction = lookup.direction();
    if (gridLineValue.allowAreaName()) {
      String searchName = gridLineValue.areaOrLineName() + lookup.areaVariant();
      int searchStart = grid.explicitSpan().lineStart(direction);
      int areaLinePos = trackPos(
        grid, direction,
        searchName,
        searchStart,
        1);
      if (isExplicitLine(grid, direction, areaLinePos)) {
        return areaLinePos;
      }
    }

    if (gridLineValue.isSpan()) {
      return null;
    }

    int searchStart = gridLineValue.lineNumber() > 0 ?
      grid.explicitSpan().lineStart(direction) :
      grid.explicitSpan().lineEnd(direction);
    return trackPos(
      grid, direction,
      gridLineValue.areaOrLineName(),
      searchStart,
      gridLineValue.lineNumber());
  }

  private static int trackPos(
    Grid grid,
    GridDirection direction,
    String areaOrLineName,
    int searchPos,
    int lineNumber
  ) {
    int remaining = lineNumber > 0 ?
      lineNumber : -lineNumber;
    int directionInc = lineNumber > 0 ? 1 : -1;
    remaining--;

    boolean isLineName = false;
    while (
      isExplicitLine(grid, direction, searchPos)
      && (
        !(isLineName = isLineName(grid, direction, areaOrLineName, searchPos))
        || remaining > 0)
    ) {
      searchPos += directionInc;
      if (isLineName) remaining--;
    }

    int sideRemaining = lineNumber > 0 ?
      remaining : -remaining;
    return searchPos + sideRemaining;
  }

  private static boolean isLineName(
    Grid grid,
    GridDirection direction,
    String areaOrLineName,
    int searchPos
  ) {
    if (areaOrLineName == null) return true;
    if (!isExplicitLine(grid, direction, searchPos)) return true;
    return grid.line(searchPos, direction).hasName(areaOrLineName);
  }

  private static boolean isExplicitLine(
    Grid grid,
    GridDirection direction,
    int searchPos
  ) {
    return
      searchPos >= grid.explicitSpan().lineStart(direction)
      && searchPos <= grid.explicitSpan().lineEnd(direction);
  }

  public static record LineNumberPair(
    Integer lineNumStart, Integer lineNumEnd
  ) {}

  private static record LineNumberLookup(
    CSSProperty relatedProperty,
    String areaVariant,
    GridDirection direction,
    Function<GridArea, Integer> gridAreaLine
  ) {}
  
}
