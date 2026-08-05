package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.content.grid.BackingGrid;
import net.buildabrowser.babbrowser.renderer.content.grid.Grid;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.content.grid.GridSpan;

public class GridImp implements Grid {

  private final Map<String, GridArea> gridAreas = new HashMap<>();
  private final BackingGrid<GridItem> backingGrid = new BackingGridImp<>(
    (w, h, d) -> new GridItem[d][h][w]);

  private GridSpan implicitSpan;
  private GridSpan explicitSpan;

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
  }

  @Override
  public void resizeImplicit(GridSpan span) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resizeImplicit'");
  }

  @Override
  public void placeRowLineName(String lineName, int rowNum) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'placeRowLine'");
  }

  @Override
  public void placeColumnLineName(String lineName, int colNum) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'placeColumnLine'");
  }

  @Override
  public int linePos(String name, int index) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'linePos'");
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
    int colStart, int colEnd,
    int rowStart, int rowEnd
  ) {
    for (int y = rowStart; y <= rowEnd; y++) {
      for (int x = colStart; x <= colEnd; x++) {
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
