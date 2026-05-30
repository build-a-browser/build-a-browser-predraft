package net.buildabrowser.babbrowser.render.content.table.imp.collapsed;

import static net.buildabrowser.babbrowser.render.content.table.imp.collapsed.TableCollapsedBorderAssignerUtil.computeInitialBorders;
import static net.buildabrowser.babbrowser.render.content.table.imp.collapsed.TableCollapsedBorderAssignerUtil.strongerBorder;

import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableColumn;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.content.table.TableRow;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;;

// TODO: Make everything static to avoid some memory usage? Probably doesn't matter.
// TODO: Also need to do track groups
public class TableCollapsedBorderTrackTableAssigner {

  private final TableCollapsedBorderAssigner mainAssigner;

  public TableCollapsedBorderTrackTableAssigner(
    TableCollapsedBorderAssigner mainAssigner
  ) {
    this.mainAssigner = mainAssigner;
  }
  
  void assignColumnsBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    for (int x = 0; x < table.width(); x++) {
      assignColumnBorder(table, referenceConstraint, x);
    }
  }

  private void assignColumnBorder(
    Table table,
    LayoutConstraint referenceConstraint,
    int x
  ) {
    TableColumn column = table.column(x);
    TableComputedBorders borders = computeInitialBorders(
      column.columnBox(), column.borders(), referenceConstraint);
    placeTopBorder(x, 0, borders.topBorder);
    placeBottomBorder(x, table.height() - 1, borders.bottomBorder);
    for (int y = 0; y < table.height(); y++) {
      placeLeftBorder(x, y, borders.leftBorder);
      placeRightBorder(x, y, borders.rightBorder);
    }
  }

  void assignRowsBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    for (int y = 0; y < table.height(); y++) {
      assignRowBorder(table, referenceConstraint, y);
    }
  }

  private void assignRowBorder(
    Table table,
    LayoutConstraint referenceConstraint,
    int y
  ) {
    TableRow row = table.row(y);
    TableComputedBorders borders = computeInitialBorders(
      row.rowBox(), row.borders(), referenceConstraint);
    placeLeftBorder(0, y, borders.leftBorder);
    placeRightBorder(table.width() - 1, y, borders.rightBorder);
    for (int x = 0; x < table.width(); x++) {
      placeTopBorder(x, y, borders.topBorder);
      placeBottomBorder(x, y, borders.bottomBorder);
    }
  }

  void assignTableBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    TableComputedBorders borders = table.borders();
    for (int x = 0; x < table.width(); x++) {
      placeTopBorder(x, 0, borders.topBorder);
      placeBottomBorder(x, table.height() - 1, borders.bottomBorder);
    }
    for (int y = 0; y < table.height(); y++) {
      placeLeftBorder(0, y, borders.leftBorder);
      placeRightBorder(table.width() - 1, y, borders.rightBorder);
    }
  }

  private void placeTopBorder(int x, int y, ComputedBorder border) {
    TableComputedBorders[][] slotGrid = mainAssigner.slotGrid();
    slotGrid[y][x].topBorder = strongerBorder(border, slotGrid[y][x].topBorder);
  }

  private void placeBottomBorder(int x, int y, ComputedBorder border) {
    TableComputedBorders[][] slotGrid = mainAssigner.slotGrid();
    slotGrid[y][x].bottomBorder = strongerBorder(border, slotGrid[y][x].bottomBorder);
  }

  private void placeLeftBorder(int x, int y, ComputedBorder border) {
    TableComputedBorders[][] slotGrid = mainAssigner.slotGrid();
    slotGrid[y][x].leftBorder = strongerBorder(border, slotGrid[y][x].leftBorder);
  }

  private void placeRightBorder(int x, int y, ComputedBorder border) {
    TableComputedBorders[][] slotGrid = mainAssigner.slotGrid();
    slotGrid[y][x].rightBorder = strongerBorder(border, slotGrid[y][x].rightBorder);
  }

}
