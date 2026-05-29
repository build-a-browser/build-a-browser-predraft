package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;

public final class TableCellMetrics {
  
  private TableCellMetrics() {}

  public static float outerCellWidth(Table table, TableCell cell) {
    float totalOuterWidth = 0;
    for (int x = cell.cellX(); x < cell.cellX() + cell.width(); x++) {
      totalOuterWidth += table.column(x).usedWidth();
    }

    return totalOuterWidth;
  }

  public static float outerCellHeight(Table table, TableCell cell) {
    float totalOuterHeight = 0;
    for (int y = cell.cellY(); y < cell.cellY() + cell.height(); y++) {
      totalOuterHeight += table.row(y).usedHeight();
    }

    return totalOuterHeight;
  }

}
