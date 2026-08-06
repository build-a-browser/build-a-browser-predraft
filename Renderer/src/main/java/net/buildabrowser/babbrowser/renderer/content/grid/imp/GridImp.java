package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.content.grid.BackingGrid;
import net.buildabrowser.babbrowser.renderer.content.grid.Grid;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.content.grid.GridLine;
import net.buildabrowser.babbrowser.renderer.content.grid.GridSpan;
import net.buildabrowser.babbrowser.renderer.content.grid.GridTrack;

public class GridImp implements Grid {

  private final Map<String, GridArea> gridAreas = new HashMap<>();
  private final BackingGrid<GridItem> backingGrid = new BackingGridImp<>(
    (w, h, d) -> new GridItem[d][h][w]);

  private GridSpan implicitSpan;
  private GridSpan explicitSpan;

  private GridTrack[] columns;
  private GridTrack[] rows;
  private GridLine[] columnLines;
  private GridLine[] rowLines;

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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resizeImplicit'");
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
  public GridLine columnLine(int colNum) {
    return columnLines[colNum - implicitSpan.colStart()];
  }

  @Override
  public GridLine rowLine(int rowNum) {
    return rowLines[rowNum - implicitSpan.rowStart()];
  }

  @Override
  public void addArea(GridArea area) {
    gridAreas.put(area.name(), area);
  }

  @Override
  public GridArea area(String areaName) {
    return gridAreas.get(areaName);
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
      backingGrid.resizeLayers(layerPos);
    }

    backingGrid.set(itemX, itemY, layerPos, item);
  }
  
}
