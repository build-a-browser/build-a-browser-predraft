package net.buildabrowser.babbrowser.render.content.table.imp.collapsed;

import static net.buildabrowser.babbrowser.render.content.table.imp.collapsed.TableCollapsedBorderAssignerUtil.computeInitialBorders;
import static net.buildabrowser.babbrowser.render.content.table.imp.collapsed.TableCollapsedBorderAssignerUtil.strongerBorder;

import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;

// TODO: Make everything static to avoid some memory usage? Probably doesn't matter.
public class TableCollapsedBorderCellAssigner {

  private final TableCollapsedBorderAssigner mainAssigner;

  public TableCollapsedBorderCellAssigner(
    TableCollapsedBorderAssigner mainAssigner
  ) {
    this.mainAssigner = mainAssigner;
  }
  
  void assignCellBorders(
    Table table,
    TableCell cell
  ) {
    TableComputedBorders borders = computeInitialBorders(
      cell.cellBox(), cell.borders());
    for (int y = 0; y < cell.height(); y++) {
      for (int x = 0; x < cell.width(); x++) {
        assignSlotCellBorders(cell, x, y, borders);
      }
    }

    unifyCellBordersForLayout(cell, borders);
  }

  // Pre-unify borders before TableCollapsedBorderAssigner
  // Since the spec failed to account for row/column borders
  // TODO: This might be affected by overlapping cells, dunno if that is desired
  private void unifyCellBordersForLayout(
    TableCell cell, TableComputedBorders borders
  ) {
    TableComputedBorders[][] slotGrid = mainAssigner.slotGrid();

    ComputedBorder topBorder = null;
    for (int x = 0; x < cell.width(); x++) {
      TableComputedBorders slot = slotGrid[cell.cellY()][x + cell.cellX()];
      topBorder = strongerBorder(topBorder, slot.topBorder);
    }
    borders.topBorder = topBorder;

    ComputedBorder bottomBorder = null;
    for (int x = 0; x < cell.width(); x++) {
      TableComputedBorders slot = slotGrid[cell.cellY() + cell.height() - 1][x + cell.cellX()];
      bottomBorder = strongerBorder(bottomBorder, slot.bottomBorder);
    }
    borders.bottomBorder = bottomBorder;

    ComputedBorder leftBorder = null;
    for (int y = 0; y < cell.height(); y++) {
      TableComputedBorders slot = slotGrid[y + cell.cellY()][cell.cellX()];
      leftBorder = strongerBorder(leftBorder, slot.leftBorder);
    }
    borders.leftBorder = leftBorder;

    ComputedBorder rightBorder = null;
    for (int y = 0; y < cell.height(); y++) {
      TableComputedBorders slot = slotGrid[y + cell.cellY()][cell.cellX() + cell.width() - 1];
      rightBorder = strongerBorder(rightBorder, slot.rightBorder);
    }
    borders.rightBorder = rightBorder;
  }

  private void assignSlotCellBorders(
    TableCell cell,
    int x, int y,
    TableComputedBorders borders
  ) {
    TableComputedBorders slot = mainAssigner.slotGrid()[y + cell.cellY()][x + cell.cellX()];
    slot.topBorder = y == 0 ? strongerBorder(borders.topBorder, slot.topBorder) : null;
    slot.bottomBorder = y == cell.height() - 1 ? strongerBorder(borders.bottomBorder, slot.bottomBorder) : null;
    slot.leftBorder = x == 0 ? strongerBorder(borders.leftBorder, slot.leftBorder) : null;
    slot.rightBorder = x == cell.width() - 1 ? strongerBorder(borders.rightBorder, slot.rightBorder) : null;
  }

}
