package net.buildabrowser.babbrowser.renderer.content.table.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;

public class TableComparator {

  private final ElementBox[][][] cells;
  private final List<Float> columnWidths;
  private final List<Float> rowHeights;
  
  public TableComparator(
    List<Float> columnWidths,
    List<Float> rowHeights,
    int layers
  ) {
    this.cells = new ElementBox[layers][rowHeights.size()][columnWidths.size()];
    this.columnWidths = columnWidths;
    this.rowHeights = rowHeights;
  }

  public void setElementBox(
    int x, int y, int z, ElementBox elementBox
  ) {
    cells[z][y][x] = elementBox;
  }

  public void compare(Table table) {
    Assertions.assertEquals(columnWidths.size(), table.width());
    Assertions.assertEquals(rowHeights.size(), table.height());
    for (int i = 0; i < columnWidths.size(); i++) {
      Assertions.assertEquals(
        columnWidths.get(i),
        table.column(i).usedWidth());
    }
    for (int i = 0; i < rowHeights.size(); i++) {
      Assertions.assertEquals(
        rowHeights.get(i),
        table.row(i).usedHeight());
    }

    for (int y = 0; y < rowHeights.size(); y++) {
      for (int x = 0; x < columnWidths.size(); x++) {
        for (int z = 0; z < cells.length; z++) {
          ElementBox expected = cells[z][y][x];
          TableCell actual = table.cell(x, y, z);
          if (expected == null) {
            Assertions.assertNull(actual);
          } else {
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(expected, actual.cellBox());
          }
        }

        Assertions.assertNull(table.cell(x, y, cells.length));
      }
    }
  }

}
