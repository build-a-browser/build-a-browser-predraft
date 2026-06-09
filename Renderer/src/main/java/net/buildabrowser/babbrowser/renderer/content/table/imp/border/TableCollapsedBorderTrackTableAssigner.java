package net.buildabrowser.babbrowser.renderer.content.table.imp.border;

import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.computeInitialBorders;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.strongerBorder;

import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableColumn;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableRow;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.ComputedBorder;;

// TODO: Need to do track groups
public final class TableCollapsedBorderTrackTableAssigner {

  private TableCollapsedBorderTrackTableAssigner() {}

  public static void assignColumnsBorders(
    TableComputedBorders[][] slotGrid,
    Table table
  ) {
    for (int x = 0; x < table.width(); x++) {
      assignColumnBorder(slotGrid, table, x);
    }
  }

  private static void assignColumnBorder(
    TableComputedBorders[][] slotGrid,
    Table table, int x
  ) {
    TableColumn column = table.column(x);
    TableComputedBorders borders = computeInitialBorders(
      column.columnBox(), column.borders());
    placeTopBorder(slotGrid, x, 0, borders.topBorder);
    placeBottomBorder(slotGrid, x, table.height() - 1, borders.bottomBorder);
    for (int y = 0; y < table.height(); y++) {
      placeLeftBorder(slotGrid, x, y, borders.leftBorder);
      placeRightBorder(slotGrid, x, y, borders.rightBorder);
    }
  }

  public static void assignRowsBorders(
    TableComputedBorders[][] slotGrid,
    Table table
  ) {
    for (int y = 0; y < table.height(); y++) {
      assignRowBorder(slotGrid, table, y);
    }
  }

  private static void assignRowBorder(
    TableComputedBorders[][] slotGrid,
    Table table, int y
  ) {
    TableRow row = table.row(y);
    TableComputedBorders borders = computeInitialBorders(
      row.rowBox(), row.borders());
    placeLeftBorder(slotGrid, 0, y, borders.leftBorder);
    placeRightBorder(slotGrid, table.width() - 1, y, borders.rightBorder);
    for (int x = 0; x < table.width(); x++) {
      placeTopBorder(slotGrid, x, y, borders.topBorder);
      placeBottomBorder(slotGrid, x, y, borders.bottomBorder);
    }
  }

  public static void assignTableBorders(
    TableComputedBorders[][] slotGrid,
    Table table
  ) {
    TableComputedBorders borders = table.borders();
    for (int x = 0; x < table.width(); x++) {
      placeTopBorder(slotGrid, x, 0, borders.topBorder);
      placeBottomBorder(slotGrid, x, table.height() - 1, borders.bottomBorder);
    }
    for (int y = 0; y < table.height(); y++) {
      placeLeftBorder(slotGrid, 0, y, borders.leftBorder);
      placeRightBorder(slotGrid, table.width() - 1, y, borders.rightBorder);
    }
  }

  private static void placeTopBorder(
    TableComputedBorders[][] slotGrid,
    int x, int y, ComputedBorder border
  ) {
    slotGrid[y][x].topBorder = strongerBorder(border, slotGrid[y][x].topBorder);
  }

  private static void placeBottomBorder(
    TableComputedBorders[][] slotGrid,
    int x, int y, ComputedBorder border
  ) {
    slotGrid[y][x].bottomBorder = strongerBorder(border, slotGrid[y][x].bottomBorder);
  }

  private static void placeLeftBorder(
    TableComputedBorders[][] slotGrid,
    int x, int y, ComputedBorder border
  ) {
    slotGrid[y][x].leftBorder = strongerBorder(border, slotGrid[y][x].leftBorder);
  }

  private static void placeRightBorder(
    TableComputedBorders[][] slotGrid,
    int x, int y, ComputedBorder border
  ) {
    slotGrid[y][x].rightBorder = strongerBorder(border, slotGrid[y][x].rightBorder);
  }

}
