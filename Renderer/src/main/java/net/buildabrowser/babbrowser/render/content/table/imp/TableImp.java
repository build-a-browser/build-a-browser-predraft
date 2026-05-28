package net.buildabrowser.babbrowser.render.content.table.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableBoxUtil;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableColumn;
import net.buildabrowser.babbrowser.render.content.table.TableContent.BorderSpacings;
import net.buildabrowser.babbrowser.render.content.table.TableRow;

public class TableImp implements Table {

  private final ElementBox tableBox;
  private final BorderSpacings spacings;

  // Multiple layers in case of overlapping cells
  private TableCellImp[][][] cells;

  private int width = -1, height = -1;
  private List<TableColumn> columns;
  private List<TableRow> rows;

  public TableImp(
    ElementBox tableBox,
    BorderSpacings spacings
  ) {
    this.tableBox = tableBox;
    this.spacings = spacings;
    this.cells = new TableCellImp[1][4][4];
  }

  @Override
  public boolean isSlotAssigned(int x, int y) {
    if (y >= cells[0].length || x >= cells[0][0].length) return false;
    return cells[0][y][x] != null;
  }

  @Override
  public void assignRowGroup(RowGroup group) {
    // TODO Auto-generated method stub
  }

  @Override
  public TableCell createCell(int cellX, int cellY, int initWidth, int initHeight, ElementBox cellBox) {
    resizeInternalGrid(cellX + initWidth, cellY + initHeight, 0);
    TableCellImp myCell = new TableCellImp(
      cellX, cellY, initWidth, initHeight,
      this, cellBox);
    for (int y = cellY; y < cellY + initHeight; y++) {
      for (int x = cellX; x < cellX + initWidth; x++) {
        recordCell(x, y, myCell);
      }
    }
    return myCell;
  }
  
  @Override
  public TableCell cell(int cellX, int cellY, int layer) {
    if (layer >= cells.length) return null;
    return cells[layer][cellY][cellX];
  }

  @Override
  public void extendCellY(TableCell cell, int targetY) {
    resizeInternalGrid(0, targetY, 0);
    TableCellImp cellImp = (TableCellImp) cell;
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
  public void createTracks() {
    this.columns = new ArrayList<>(width);
    int specWidth = createSpecifiedColumns(0, tableBox);
    for (int x = specWidth; x < width; x++) {
      ElementBox colBox = ElementBox.createAnonymous(tableBox, BoxLevel.INLINE_LEVEL);
      columns.add(new TableColumnImp(this, x, colBox));
    }

    this.rows = new ArrayList<>();
    createSpecifiedRows(0, tableBox);
  }

  @Override
  public List<TableColumn> columns() {
    assert this.columns != null;
    return columns;
  }

  @Override
  public List<TableRow> rows() {
    assert this.rows != null;
    return rows;
  }

  @Override
  public List<ColumnGroup> columnGroups() {
    // TODO: Implement
    return List.of();
  }

  @Override
  public List<RowGroup> rowGroups() {
    // TODO: Implement
    return List.of();
  }

  @Override
  public TableColumn column(int colX) {
    assert this.columns != null;
    return columns.get(colX);
  }

  @Override
  public TableRow row(int rowY) {
    assert this.rows != null;
    return rows.get(rowY);
  }

  @Override
  public BorderSpacings spacings() {
    return this.spacings;
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

  private void recordCell(int cellX, int cellY, TableCellImp myCell) {
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

    TableCellImp[][][] newCells = new TableCellImp[newDepth][newHeight][newWidth];
    for (int z = 0; z < this.cells.length; z++) {
      for (int y = 0; y < this.cells[0].length; y++) {
        System.arraycopy(this.cells[z][y], 0, newCells[z][y], 0, this.cells[z][y].length);
      }
    }
    this.cells = newCells;
  }

  private int createSpecifiedColumns(int x, ElementBox parentBox) {
    ElementBoxIterator childIt = parentBox.childBoxes();
    while (childIt.hasNext() && x < width) {
      Box nextBox = childIt.next();
      if (
        nextBox instanceof ElementBox elBox
        && TableBoxUtil.isColumnGroup(nextBox)
      ) {
        x = createSpecifiedColumns(x, elBox);
      } else if (
        nextBox instanceof ElementBox colBox
        && TableBoxUtil.isTableColumn(nextBox)
      ) {
        columns.add(new TableColumnImp(this, x, colBox));
        x++;
      }
    }

    return x;
  }

  private int createSpecifiedRows(int x, ElementBox parentBox) {
    ElementBoxIterator childIt = parentBox.childBoxes();
    while (childIt.hasNext() && x < width) {
      Box nextBox = childIt.next();
      if (
        nextBox instanceof ElementBox elBox
        && TableBoxUtil.isTableRowGroup(nextBox)
      ) {
        x = createSpecifiedRows(x, elBox);
      } else if (
        nextBox instanceof ElementBox rowBox
        && TableBoxUtil.isTableRow(nextBox)
      ) {
        rows.add(new TableRowImp(rowBox));
        x++;
      }
    }

    return x;
  }
  
}
