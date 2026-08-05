package net.buildabrowser.babbrowser.renderer.content.grid.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.Grid;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.content.grid.GridSpan;

public class GridComparator {

  private final GridSpan implicitSpan;
  private final GridSpan explicitSpan;
  private final ElementBox[][][] cells;
  @SuppressWarnings("unused")
  private final List<Float> columnWidths;
  @SuppressWarnings("unused")
  private final List<Float> rowHeights;
  
  public GridComparator(
    GridSpan implicitSpan,
    GridSpan explicitSpan,
    List<Float> columnWidths,
    List<Float> rowHeights,
    int layers
  ) {
    this.implicitSpan = implicitSpan;
    this.explicitSpan = explicitSpan;
    this.columnWidths = columnWidths;
    this.rowHeights = rowHeights;
    this.cells = new ElementBox[layers][implicitSpan.height()][implicitSpan.width()];
  }

  public GridComparator(
    GridSpan explicitSpan,
    List<Float> columnWidths,
    List<Float> rowHeights,
    int layers
  ) {
    this(explicitSpan, explicitSpan, columnWidths, rowHeights, layers);
  }

  public GridComparator(
    GridSpan explicitSpan,
    int layers
  ) {
    this(explicitSpan, explicitSpan, null, null, layers);
  }

  public void setElementBox(
    int x, int y, int z, ElementBox elementBox
  ) {
    int xA = x - implicitSpan.colStart();
    int yA = y - implicitSpan.rowStart();
    cells[z][yA][xA] = elementBox;
  }

  public void compare(Grid grid) {
    Assertions.assertEquals(grid.implicitSpan(), this.implicitSpan);
    Assertions.assertEquals(grid.explicitSpan(), this.explicitSpan);
    /*for (int i = 0; i < columnWidths.size(); i++) {
      Assertions.assertEquals(
        columnWidths.get(i),
        grid.column(i).usedWidth());
    }
    for (int i = 0; i < rowHeights.size(); i++) {
      Assertions.assertEquals(
        rowHeights.get(i),
        grid.row(i).usedHeight());
    }*/

    for (int y = implicitSpan.rowStart(); y <= implicitSpan.rowEnd(); y++) {
      for (int x = implicitSpan.colStart(); x <= implicitSpan.colEnd(); x++) {
        for (int z = 0; z < cells.length; z++) {
          int xA = x - implicitSpan.colStart();
          int yA = y - implicitSpan.rowStart();
          ElementBox expected = cells[z][yA][xA];
          GridItem actual = grid.cell(x, y, z);
          if (expected == null) {
            Assertions.assertNull(actual);
          } else {
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(expected, actual.itemBox());
          }
        }

        Assertions.assertNull(grid.cell(x, y, cells.length));
      }
    }
  }

}
