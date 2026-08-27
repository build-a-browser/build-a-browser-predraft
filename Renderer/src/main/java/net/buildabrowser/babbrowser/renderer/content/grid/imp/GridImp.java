package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import java.util.List;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.BackingGrid;
import net.buildabrowser.babbrowser.renderer.content.grid.Grid;
import net.buildabrowser.babbrowser.renderer.content.grid.GridDirection;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.content.grid.GridLine;
import net.buildabrowser.babbrowser.renderer.content.grid.GridSpan;
import net.buildabrowser.babbrowser.renderer.content.grid.GridTrack;

public class GridImp implements Grid {

  private final BackingGrid<GridItem> backingGrid = new BackingGridImp<>(
    (w, h, d) -> new GridItem[d][h][w]);

  private final ElementBox gridBox;

  private GridSpan implicitSpan;
  private GridSpan explicitSpan;

  private GridTrack[] columns;
  private GridTrack[] rows;
  private GridLine[] columnLines;
  private GridLine[] rowLines;

  public GridImp(ElementBox gridBox) {
    this.gridBox = gridBox;
  }

  @Override
  public ElementBox gridBox() {
    return this.gridBox;
  }

  @Override
  public GridSpan explicitSpan() {
    return this.explicitSpan;
  }

  @Override
  public GridSpan implicitSpan() {
    return this.implicitSpan;
  }

  @Override
  public void resizeExplicit(GridSpan span) {
    this.explicitSpan = span;
    this.implicitSpan = span;
    backingGrid.resize(span);

    // resizeExplicit is only called once, so don't bother resizing existing array
    assert this.columns == null;
    assert this.rows == null;

    this.columns = new GridTrack[span.width()];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = GridTrack.createExplicit();
    }

    this.columnLines = new GridLine[span.width() + 1];
    for (int i = 0; i < columnLines.length; i++) {
      columnLines[i] = GridLine.createExplicit();
    }

    this.rows = new GridTrack[span.height()];
    for (int i = 0; i < rows.length; i++) {
      rows[i] = GridTrack.createExplicit();
    }

    this.rowLines = new GridLine[span.height() + 1];
    for (int i = 0; i < rowLines.length; i++) {
      rowLines[i] = GridLine.createExplicit();
    }
  }

  @Override
  public void resizeImplicit(GridSpan span) {
    if (span.equals(implicitSpan)) return;

    int colDiff = this.implicitSpan.colLineStart() - span.colLineStart();
    this.columns = resizeImplicit(
      new GridTrack[span.width()], this.columns, colDiff,
      GridTrack::createImplicit);
    this.columnLines = resizeImplicit(
      new GridLine[span.width() + 1], this.columnLines, colDiff,
      GridLine::createImplicit);
    
    int rowDiff = this.implicitSpan.rowLineStart() - span.rowLineStart();
    this.rows = resizeImplicit(
      new GridTrack[span.height()], this.rows, rowDiff,
      GridTrack::createImplicit);
    this.rowLines = resizeImplicit(
      new GridLine[span.height() + 1], this.rowLines, rowDiff,
      GridLine::createImplicit);

    backingGrid.resize(span);
    this.implicitSpan = span;
  }

  @Override
  public GridTrack column(int colNum) {
    return columns[colNum - implicitSpan.colStart()];
  }

  @Override
  public GridTrack row(int rowNum) {
    return rows[rowNum - implicitSpan.rowStart()];
  }

  @Override
  public GridTrack track(int trackNum, GridDirection direction) {
    return switch (direction) {
      case COLUMN -> column(trackNum);
      case ROW -> row(trackNum);
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  @Override
  public GridTrack[] tracks(GridDirection direction) {
    return switch (direction) {
      case COLUMN -> columns;
      case ROW -> rows;
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  @Override
  public GridLine columnLine(int colNum) {
    return columnLines[colNum - implicitSpan.colStart()];
  }

  @Override
  public GridLine rowLine(int rowNum) {
    return rowLines[rowNum - implicitSpan.rowStart()];
  }

  @Override
  public GridLine line(int lineNum, GridDirection direction) {
    return switch (direction) {
      case COLUMN -> columnLine(lineNum);
      case ROW -> rowLine(lineNum);
      default -> throw new IllegalArgumentException(
        "Not a valid grid direction: " + direction);
    };
  }

  @Override
  public void addArea(GridArea area) {
    columnLine(area.x()).addNames(List.of(area.name() + "-start"));
    columnLine(area.x() + area.w()).addNames(List.of(area.name() + "-end"));
    rowLine(area.y()).addNames(List.of(area.name() + "-start"));
    rowLine(area.y() + area.h()).addNames(List.of(area.name() + "-end"));
  }

  @Override
  public void placeItem(
    GridItem item,
    int colLineStart, int colLineEnd,
    int rowLineStart, int rowLineEnd
  ) {
    for (int y = rowLineStart; y < rowLineEnd; y++) {
      for (int x = colLineStart; x < colLineEnd; x++) {
        placeItemAtCell(item, x, y);
      }
    }
  }

  @Override
  public GridItem cell(int x, int y, int z) {
    if (z >= backingGrid.layers()) return null;
    return backingGrid.item(x, y, z);
  }

  @Override
  public boolean isOccupied(int x, int y) {
    if (
      x < implicitSpan.colStart()
      || x > implicitSpan.colEnd()
      || y < implicitSpan.rowStart()
      || y > implicitSpan.rowEnd()
    ) return false;

    return cell(x, y, 0) != null;
  }

  private void placeItemAtCell(
    GridItem item,
    int itemX,
    int itemY
  ) {
    int layerPos = 0;
    while (
      layerPos < backingGrid.layers()
      && backingGrid.item(itemX, itemY, layerPos) != null
    ) layerPos++;

    if (layerPos >= backingGrid.layers()) {
      backingGrid.resizeLayers(layerPos + 1);
    }

    backingGrid.set(itemX, itemY, layerPos, item);
  }

  private <T> T[] resizeImplicit(
    T[] newValues, T[] oldValues, int sizeDiff,
    Supplier<T> tSupplier
  ) {
    for (int i = 0; i < newValues.length; i++) {
      if (i >= sizeDiff && i < sizeDiff + oldValues.length) {
        newValues[i] = oldValues[i - sizeDiff];
      } else {
        newValues[i] = tSupplier.get();
      }
    }

    return newValues;
  }
  
}
