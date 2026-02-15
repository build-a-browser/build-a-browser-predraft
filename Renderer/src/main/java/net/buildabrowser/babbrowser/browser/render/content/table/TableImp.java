package net.buildabrowser.babbrowser.browser.render.content.table;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class TableImp implements Table {

  // Multiple layers in case of overlapping cells
  private CellImp[][][] cells;

  private int width = -1, height = -1;

  public TableImp() {
    this.cells = new CellImp[1][4][4];
  }

  @Override
  public boolean isSlotAssigned(int x, int y) {
    if (x >= width || y >= height) return false;
    return cells[0][y][x] != null;
  }

  @Override
  public void assignRowGroup(RowGroup group) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Cell createCell(int cellX, int cellY, int initWidth, int initHeight, ElementBox cellBox) {
    resizeInternalGrid(cellX + initWidth, cellY + initHeight, 0);
    CellImp myCell = new CellImp(cellX, cellY, initWidth, initHeight, cellBox);
    for (int y = cellY; y < cellY + initHeight; y++) {
      for (int x = cellX; x < cellX + initWidth; x++) {
        recordCell(cellX, cellY, myCell);
      }
    }
    return myCell;
  }
  
  @Override
  public Cell getCell(int cellX, int cellY, int layer) {
    if (layer >= cells.length) return null;
    return cells[layer][cellY][cellX];
  }

  @Override
  public void extendCellY(Cell cell, int targetY) {
    resizeInternalGrid(0, targetY, 0);
    CellImp cellImp = (CellImp) cell;
    for (int y = cell.cellY() + cell.height(); y <= targetY; y++) {
      cellImp.extend(1);
      for (int x = cell.cellX(); x < cell.cellX() + cell.width(); x++) {
        recordCell(x, y, cellImp);
      }
    }
  }

  @Override
  public void markSize(int width, int height) {
    // TODO: Should the table shrink? (memory vs speed)
    this.width = width;
    this.height = height;
  }

  @Override
  public int width() {
    assert width != -1;
    return this.width;
  }

  @Override
  public int height() {
    assert height != -1;
    return this.height;
  }

  private void recordCell(int cellX, int cellY, CellImp myCell) {
    for (int z = 0; z < cells.length; z++) {
      if (cells[z][cellY][cellX] != null) continue;
      cells[z][cellY][cellX] = myCell;
      return;
    }

    // TODO: Avoid resizing twice
    resizeInternalGrid(0, 0, cells.length + 1);
    cells[cells.length - 1][cellY][cellX] = myCell;
  }

  private void resizeInternalGrid(int width, int height, int depth) {
    int newDepth = Math.max(depth, this.cells.length);
    int newHeight = this.cells[0].length;
    while (newHeight < height) newHeight *= 2;
    int newWidth = this.cells[0][0].length;
    while (newWidth < width) newWidth *= 2;

    if (
      newDepth == this.cells.length
      && newHeight == this.cells[0].length
      && newWidth == this.cells[0][0].length
    ) return;

    CellImp[][][] newCells = new CellImp[newDepth][newHeight][newWidth];
    for (int z = 0; z < this.cells.length; z++) {
      for (int y = 0; y < this.cells[0].length; y++) {
        System.arraycopy(this.cells[z][y], 0, newCells[z][y], 0, this.cells[z][y].length);
      }
    }
    this.cells = newCells;
  }
  
}
