package net.buildabrowser.babbrowser.renderer.content.grid;

import static net.buildabrowser.babbrowser.renderer.content.grid.GridLineResolver.itemAutoEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.content.grid.GridLineResolver.LineNumberPair;

public final class GridItemPlacer {
  
  private GridItemPlacer() {}

  public static void placeGridElements(
    Grid grid,
    List<GridItem> gridItems
  ) {
    List<GridItem> gridItemQueue = new ArrayList<>(gridItems.size());
    gridItemQueue.addAll(gridItems);

    placeGridAreas(grid);
    determineManualItemPositions(grid, gridItemQueue);
    createImplicitTracksForPositioned(grid, gridItemQueue);
    placeManualPositionedItems(grid, gridItemQueue);

    GridAutoFlowValue autoFlow = (GridAutoFlowValue) grid.gridBox().properties()
      .get(CSSProperty.GRID_AUTO_FLOW);
    GridDirection autoFlowDirection = autoFlow.direction().equals(GridAutoFlowDirection.ROW) ?
      GridDirection.ROW : GridDirection.COLUMN;
    boolean isDense = autoFlow.isDense();
    placeSomewhatManualPositionedItems(
      grid, gridItemQueue, autoFlowDirection, isDense);
    computeAutoSpans(grid, gridItemQueue, autoFlowDirection);
    autoPlaceItems(grid, gridItemQueue, autoFlowDirection, isDense);
  }

  private static void placeGridAreas(Grid grid) {
    CSSValue gridTemplateAreasValue = grid.gridBox().properties()
      .get(CSSProperty.GRID_TEMPLATE_AREAS);
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
      LineNumberPair colLines = GridLineResolver.itemColumnLines(grid, item);
      LineNumberPair rowLines = GridLineResolver.itemRowLines(grid, item);
      
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

    grid.resizeImplicit(GridSpan.create(
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
    ListIterator<GridItem> queueIt = gridItemQueue.listIterator();
    int[] minPos = new int[grid.implicitSpan().size(autoFlowDirection)];
    int minPosOffset = -grid.implicitSpan().trackStart(rotateDirection);
    while (queueIt.hasNext()) {
      GridItem item = queueIt.next();
      Integer defTrackStart = item.lineStart(autoFlowDirection);
      Integer defTrackEnd = item.lineEnd(autoFlowDirection);
      if (
        defTrackStart == null
        || defTrackEnd == null
      ) continue;
      queueIt.remove();

      int scanPosition = 1;
      if (!isDense) {
        for (int i = defTrackStart; i < defTrackEnd; i++) {
          scanPosition = Math.max(scanPosition, minPos[i + minPosOffset]);
        }
      }
      // TODO: If many items are not auto, dense packing will scale exponentially

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
      
      if (!isDense) {
        for (int i = defTrackStart; i < defTrackEnd; i++) {
          minPos[i + minPosOffset] = Math.max(minPos[i + minPosOffset], dirEnd);
        }
      }
    }
  }

  private static void computeAutoSpans(
    Grid grid,
    List<GridItem> gridItemQueue,
    GridDirection autoFlowDirection
  ) {
    GridDirection rotateDirection = autoFlowDirection.rotate();

    int largestSize = 0;
    int minLine = grid.implicitSpan().lineStart(rotateDirection);
    int maxLine = grid.implicitSpan().lineEnd(rotateDirection);

    for (GridItem item: gridItemQueue) {
      Integer itemStart = item.lineStart(rotateDirection);
      Integer itemEnd = item.lineEnd(rotateDirection);
      if (itemStart != null && itemEnd != null) {
        minLine = Math.min(minLine, itemStart);
        maxLine = Math.max(maxLine, itemEnd);
        item.setFallbackSpan(itemEnd - itemStart);
        continue;
      }

      CSSValue spanValue = GridLineResolver.itemSpanValue(item, rotateDirection);
      if (
        spanValue instanceof GridLineValue lineValue
        && lineValue.isSpan()
        && lineValue.areaOrLineName() == null
      ) {
        item.setFallbackSpan(lineValue.lineNumber());
      } else {
        item.setFallbackSpan(1);
      }

      largestSize = Math.max(largestSize, item.fallbackSpan());
    }

    maxLine = Math.max(maxLine, minLine + largestSize);
    GridSpan newSpan = grid.implicitSpan().withDimension(
      rotateDirection, minLine, maxLine - 1);
    grid.resizeImplicit(newSpan);
  }

  // TODO: "If the placement contains only a span for a named line, replace it with a span of 1"
  // Does the span remain 1 after auto-placement, or only before placement?
  // Right now, the rotate direction does the former, while flow direction does the latter
  // TODO: Split into smaller methods
  private static void autoPlaceItems(
    Grid grid,
    List<GridItem> gridItemQueue,
    GridDirection autoFlowDirection,
    boolean isDense
  ) {
    GridDirection rotateDirection = autoFlowDirection.rotate();
    int flowCursor = grid.implicitSpan().lineStart(autoFlowDirection);
    int rotateCursor = grid.implicitSpan().lineStart(rotateDirection);
    ListIterator<GridItem> queueIt = gridItemQueue.listIterator();
    while (queueIt.hasNext()) {
      GridItem item = queueIt.next();
      GridSpan implicitSpan = grid.implicitSpan();
      int rotateSpan = item.fallbackSpan();

      Integer lineStart = item.lineStart(rotateDirection);
      Integer lineEnd = item.lineEnd(rotateDirection);
      if (
        lineStart != null
        || lineEnd != null
      ) { // Definite column position
        int prevRotateCursor = rotateCursor;
        rotateCursor = lineStart;
        if (isDense) {
          flowCursor = implicitSpan.lineStart(autoFlowDirection);
        } else if (rotateCursor < prevRotateCursor) {
          flowCursor++;
        }

        boolean willOverlap = true;
        do {
          willOverlap = willOverlap(
            grid, item, autoFlowDirection,
            rotateCursor, rotateCursor + rotateSpan,
            flowCursor);
          
          if (willOverlap) {
            flowCursor++;
          }
        } while (willOverlap);
      } else { // Automatic grid position in both axis
        if (isDense) {
          rotateCursor = implicitSpan.lineStart(rotateDirection);
          flowCursor = implicitSpan.lineStart(autoFlowDirection);
        }
        
        boolean willOverlap = true;
        do {
          int spanEnd = rotateCursor + rotateSpan;
          if (
            spanEnd > implicitSpan.lineEnd(rotateDirection)
          ) {
            rotateCursor = implicitSpan.lineStart(rotateDirection);
            flowCursor++;
            continue;
          }

          willOverlap = willOverlap(
            grid, item, autoFlowDirection,
            rotateCursor, rotateCursor + rotateSpan,
            flowCursor);
          
          if (willOverlap) {
            rotateCursor++;
          }
        } while (willOverlap);
      }

      int spanEnd = rotateCursor + rotateSpan;
      int autoSpanEnd = itemAutoEnd(grid, item, autoFlowDirection, flowCursor);
      if (autoSpanEnd > implicitSpan.lineEnd(autoFlowDirection)) {
        grid.resizeImplicit(implicitSpan.withDimension(
          autoFlowDirection,
          implicitSpan.lineStart(autoFlowDirection),
          autoSpanEnd - 1));
      }

      if (autoFlowDirection.equals(GridDirection.ROW)) {
        item.setSpan(rotateCursor, spanEnd, flowCursor, autoSpanEnd);
      } else {
        item.setSpan(flowCursor, autoSpanEnd, rotateCursor, spanEnd);
      }

      grid.placeItem(
        item,
        item.colLineStart(), item.colLineEnd(),
        item.rowLineStart(), item.rowLineEnd());
    }
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

  // TODO: Because the implicit size and grid capacity are currently directly tied,
  // this will cause excessive copies
  private static void resizeForItem(
    Grid grid, GridItem item
  ) {
    int minColCell = Math.min(grid.implicitSpan().colStart(), item.colLineStart());
    int maxColCell = Math.max(grid.implicitSpan().colEnd(), item.colLineEnd() - 1);
    int minRowCell = Math.min(grid.implicitSpan().rowStart(), item.rowLineStart());
    int maxRowCell = Math.max(grid.implicitSpan().rowEnd(), item.rowLineEnd() - 1);

    grid.resizeImplicit(GridSpan.create(
      minColCell, maxColCell, minRowCell, maxRowCell));
  }

}
