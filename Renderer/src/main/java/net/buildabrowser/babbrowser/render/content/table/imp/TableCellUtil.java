package net.buildabrowser.babbrowser.render.content.table.imp;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;

public final class TableCellUtil {
  
  private TableCellUtil() {}

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

  public static void forEachCell(
    Table table,
    Consumer<TableCell> cellAction
  ) {
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.cellX() != x || cell.cellY() != y) continue;
          
          cellAction.accept(cell);
        }
      }
    }
  }

}
