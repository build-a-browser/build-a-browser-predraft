package net.buildabrowser.babbrowser.renderer.content.grid;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public final class GridItemPlacer {

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
  
  private GridItemPlacer() {}

  // List must be mutable
  public static void placeGridElements(
    Grid grid,
    ElementBox gridBox,
    List<GridItem> gridItemQueue
  ) {
    placeGridAreas(grid, gridBox);
    determineManualItemPositions(grid, gridItemQueue);
    createImplicitTracksForPositioned(grid, gridItemQueue);
    placeManualPositionedItems(grid, gridItemQueue);

    GridAutoFlowValue autoFlow = (GridAutoFlowValue) gridBox.properties()
      .get(CSSProperty.GRID_AUTO_FLOW);
    GridDirection autoFlowDirection = autoFlow.direction().equals(GridAutoFlowDirection.ROW) ?
      GridDirection.ROW : GridDirection.COLUMN;
    boolean isDense = autoFlow.isDense();

    placeSomewhatManualPositionedItems(
      grid, gridItemQueue, autoFlowDirection, isDense);
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

  private static void createImplicitTracksForPositioned(
    Grid grid,
    List<GridItem> gridItemQueue
  ) {
    int minColCell = grid.implicitSpan().colStart();
    int maxColCell = grid.implicitSpan().colEnd();
    int minRowCell = grid.implicitSpan().rowStart();
    int maxRowCell = grid.implicitSpan().rowEnd();
    for (GridItem item: gridItemQueue) {
      if (item.colLineStart() != null) {
        minColCell = Math.min(minColCell, item.colLineStart());
      }
      if (item.colLineEnd() != null) {
        maxColCell = Math.max(maxColCell, item.colLineEnd() - 1);
      }
      if (item.rowLineStart() != null) {
        minRowCell = Math.min(minRowCell, item.rowLineStart());
      }
      if (item.rowLineEnd() != null) {
        maxRowCell = Math.max(maxRowCell, item.rowLineEnd() - 1);
      }
    }

    grid.resizeImplicit(new GridSpan(
      minColCell, maxColCell, minRowCell, maxRowCell));
  }

  private static void placeManualPositionedItems(
    Grid grid,
    List<GridItem> gridItemQueue
  ) {
    ListIterator<GridItem> queueIt = gridItemQueue.listIterator();
    while (queueIt.hasNext()) {
      GridItem item = queueIt.next();
      if (
        item.colLineStart() != null
        && item.colLineEnd() != null
        && item.rowLineStart() != null
        && item.rowLineEnd() != null
      ) {
        grid.placeItem(
          item,
          item.colLineStart(), item.colLineEnd(),
          item.rowLineStart(), item.rowLineEnd());
        queueIt.remove();
      }
    }
  }

  private static void placeSomewhatManualPositionedItems(
    Grid grid,
    List<GridItem> gridItemQueue,
    GridDirection autoFlowDirection,
    boolean isDense
  ) {
    GridDirection rotateDirection = autoFlowDirection.rotate();
    int scanPosition = 1;
    ListIterator<GridItem> queueIt = gridItemQueue.listIterator();
    while (queueIt.hasNext()) {
      GridItem item = queueIt.next();
      Integer defTrackStart = item.lineStart(autoFlowDirection);
      Integer defTrackEnd = item.lineEnd(autoFlowDirection);
      if (
        defTrackStart == null
        || defTrackEnd == null
      ) continue;
      queueIt.remove();

      // TODO: If many items are not auto, dense packing will scale exponentially
      if (isDense) {
        scanPosition = 1;
      }

      int gridLineEnd = grid.implicitSpan().lineEnd(rotateDirection);
      while (
        scanPosition <= gridLineEnd
        && willOverlap(
          grid, item, rotateDirection,
          defTrackStart, defTrackEnd, scanPosition)
      ) {
        scanPosition++;
      }

      // TODO: willOverlap already calls this, meaning the same thing is done twice
      int dirEnd = itemAutoEnd(grid, item, rotateDirection, scanPosition);
      if (rotateDirection.equals(GridDirection.ROW)) {
        item.setSpan(defTrackStart, defTrackEnd, scanPosition, dirEnd);
      } else {
        item.setSpan(scanPosition, dirEnd, defTrackStart, defTrackEnd);
      }
      resizeForItem(grid, item);
      grid.placeItem(item,
        item.colLineStart(), item.colLineEnd(),
        item.rowLineStart(), item.rowLineEnd());
      
      scanPosition = dirEnd;
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

  private static Integer itemAutoEnd(
    Grid grid,
    GridItem item,
    GridDirection direction,
    int dirStart
  ) {
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
    
    Integer dirEnd = spanValue instanceof GridLineValue lineValue && lineValue.isSpan() ?
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

  private static boolean willOverlap(
    Grid grid,
    GridItem item,
    GridDirection direction,
    int rotateStart,
    int rotateEnd,
    int dirStart
  ) {
    int dirEnd = itemAutoEnd(grid, item, direction, dirStart);

    boolean flipCall = direction.equals(GridDirection.ROW);
    for (int rotate = rotateStart; rotate < rotateEnd; rotate++) {
      for (int dir = dirStart; dir < dirEnd; dir++) {
        if (flipCall && grid.isOccupied(rotate, dir)) {
          return true;
        } else if (!flipCall && grid.isOccupied(dir, rotate)) {
          return true;
        }
      }
    }

    return false;
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

  // TODO: Because the implicit size and grid capacity are currently directly tied,
  // this will cause excessive copies
  private static void resizeForItem(
    Grid grid, GridItem item
  ) {
    int minColCell = Math.min(grid.implicitSpan().colStart(), item.colLineStart());
    int maxColCell = Math.max(grid.implicitSpan().colEnd(), item.colLineEnd() - 1);
    int minRowCell = Math.min(grid.implicitSpan().rowStart(), item.rowLineStart());
    int maxRowCell = Math.max(grid.implicitSpan().rowEnd(), item.rowLineEnd() - 1);

    grid.resizeImplicit(new GridSpan(
      minColCell, maxColCell, minRowCell, maxRowCell));
  }

  private static record LineNumberPair(
    Integer lineNumStart, Integer lineNumEnd
  ) {}

  private static record LineNumberLookup(
    CSSProperty relatedProperty,
    String areaVariant,
    GridDirection direction,
    Function<GridArea, Integer> gridAreaLine
  ) {}

}
