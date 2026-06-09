package net.buildabrowser.babbrowser.renderer.content.table.imp.border;

import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.compareCellOrder;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.isCurrentMoreSpecific;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.strongerBorder;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.renderer.content.table.TableConflictOps;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;;

// All values are pre-divided, so don't divide by two where the spec says
public final class TableBorderConflictResolver {

  private static final TableConflictOps RIGHT_CONFLICT_OPS = new TableRightConflictOps();
  private static final TableConflictOps BOTTOM_CONFLICT_OPS = new TableBottomConflictOps();
  
  private TableBorderConflictResolver() {}

  public static void resolveBorderConflicts(Table table) {
    TableCellUtil.forEachCell(table, cell -> {
      resolveBorderConflicts(RIGHT_CONFLICT_OPS, table, cell);
      resolveBorderConflicts(BOTTOM_CONFLICT_OPS, table, cell);
    });

    resolveTableBorderConflicts(table);
  }
  
  // TODO: There is no way this is performant
  // (but tables are uncommon enough for this to be left for later...)
  private static void resolveBorderConflicts(
    TableConflictOps conflictOps,
    Table table, TableCell cell
  ) {
    Set<TableCell> cellSet1 = new TreeSet<>(TableCollapsedBorderAssignerUtil::compareCellOrder);
    Set<TableCell> cellSet2 = new TreeSet<>(TableCollapsedBorderAssignerUtil::compareCellOrder);

    int initPos = conflictOps.lockedStart(cell) + conflictOps.lockedRun(cell) - 1;
    for (int scan = conflictOps.scanStart(cell); scan < conflictOps.scanStart(cell) + conflictOps.scanRun(cell); scan++) {
      for (int z = 0; conflictOps.getCell(table, initPos, scan, z) != null; z++) {
        TableCell overlapCell = conflictOps.getCell(table, initPos, scan, z);
        int overlapPos = conflictOps.lockedStart(overlapCell) + conflictOps.lockedRun(overlapCell) - 1;
        if (overlapPos != initPos) continue;
        addSet1Cell(conflictOps, table, overlapCell, cellSet1, cellSet2);
      }
    }

    if (cellSet1.isEmpty() && cellSet2.isEmpty()) return;

    ComputedBorder border = harmonizeCells(conflictOps, cellSet1, cellSet2);
    for (TableCell set1Cell: cellSet1) {
      conflictOps.set1Border(set1Cell, border);
    }
    for (TableCell set2Cell: cellSet2) {
      conflictOps.set2Border(set2Cell, border);
    }
  }

  private static void addSet1Cell(
    TableConflictOps conflictOps,
    Table table, TableCell cell,
    Set<TableCell> cellSet1, Set<TableCell> cellSet2
  ) {
    if (!cellSet1.add(cell)) return;

    BorderSide sourceSide = conflictOps.get1Border(cell).sourceSide();
    if (!(
      sourceSide.equals(BorderSide.RIGHT)
      || sourceSide.equals(BorderSide.BOTTOM)
    )) return;

    // Must have already been harmonized if border source is the opposite side
    int borderStart = conflictOps.lockedStart(cell) + conflictOps.lockedRun(cell);
    if (borderStart >= conflictOps.lockedRun(table)) return;
    for (int scan = conflictOps.scanStart(cell); scan < conflictOps.scanStart(cell) + conflictOps.scanRun(cell); scan++) {
      for (int z = 0; conflictOps.getCell(table, borderStart, scan, z) != null; z++) {
        TableCell borderCell = conflictOps.getCell(table, borderStart, scan, z);
        if (conflictOps.lockedStart(borderCell) != borderStart) continue;
        addSet2Cell(conflictOps, table, borderCell, cellSet1, cellSet2);
      }
    }
  }

  private static void addSet2Cell(
    TableConflictOps conflictOps,
    Table table, TableCell cell,
    Set<TableCell> cellSet1, Set<TableCell> cellSet2
  ) {
    if (!cellSet2.add(cell)) return;

    BorderSide sourceSide = conflictOps.get2Border(cell).sourceSide();
    if (!(
      sourceSide.equals(BorderSide.LEFT)
      || sourceSide.equals(BorderSide.TOP)
    )) return;

    int borderStart = conflictOps.lockedStart(cell) - 1;
    if (borderStart < 0) return;
    for (int scan = conflictOps.scanStart(cell); scan < conflictOps.scanStart(cell) + conflictOps.scanRun(cell); scan++) {
      for (int z = 0; conflictOps.getCell(table, borderStart, scan, z) != null; z++) {
        TableCell borderCell = conflictOps.getCell(table, borderStart, scan, z);
        int overlapStart = conflictOps.lockedStart(borderCell) + conflictOps.lockedRun(borderCell) - 1;
        if (overlapStart != borderStart) continue;
        addSet1Cell(conflictOps, table, borderCell, cellSet1, cellSet2);
      }
    }
  }

  private static ComputedBorder harmonizeCells(
    TableConflictOps conflictOps,
    Set<TableCell> cellSet1, Set<TableCell> cellSet2
  ) {

    Iterator<TableCell> set1It = cellSet1.iterator();
    Iterator<TableCell> set2It = cellSet2.iterator();

    // Though the spec mentions a starting point, this is simpler
    ComputedBorder chosenBorder = null;

    TableCell nextCell1 = set1It.hasNext() ? set1It.next() : null;
    TableCell nextCell2 = set2It.hasNext() ? set2It.next() : null;
    while (nextCell1 != null || nextCell2 != null) {
      ComputedBorder currentBorder;
      if (nextCell2 == null) {
        currentBorder = conflictOps.get1Border(nextCell1);
        nextCell1 = set1It.hasNext() ? set1It.next() : null;
      } else if (nextCell1 == null) {
        currentBorder = conflictOps.get2Border(nextCell2);
        nextCell2 = set2It.hasNext() ? set2It.next() : null;
      } else if (compareCellOrder(nextCell1, nextCell2) < 1) {
        currentBorder = conflictOps.get1Border(nextCell1);
        nextCell1 = set1It.hasNext() ? set1It.next() : null;
      } else {
        currentBorder = conflictOps.get2Border(nextCell2);
        nextCell2 = set2It.hasNext() ? set2It.next() : null;
      }

      if (
        chosenBorder == null
        || isCurrentMoreSpecific(chosenBorder, currentBorder)
      ) {
        chosenBorder = currentBorder;
      }
    }

    // TODO: Tracks and track groups

    return chosenBorder;
  }

  private static void resolveTableBorderConflicts(Table table) {
    TableComputedBorders borders = table.borders();

    ComputedBorder largestTopBorder = borders.topBorder;
    for (int x = 0; x < table.width(); x++) {
      for (int z = 0; table.cell(x, 0, z) != null; z++) {
        TableComputedBorders cellBorders = table.cell(x, 0, z).borders();
        cellBorders.topBorder = strongerBorder(cellBorders.topBorder, borders.topBorder);
        largestTopBorder = strongerBorder(largestTopBorder, cellBorders.topBorder);
      }
    }

    ComputedBorder largestBottomBorder = borders.bottomBorder;
    for (int x = 0; x < table.width(); x++) {
      for (int z = 0; table.cell(x, table.height() - 1, z) != null; z++) {
        TableComputedBorders cellBorders = table.cell(x, table.height() - 1, z).borders();
        cellBorders.bottomBorder = strongerBorder(cellBorders.bottomBorder, borders.bottomBorder);
        largestBottomBorder = strongerBorder(largestBottomBorder, cellBorders.bottomBorder);
      }
    }

    ComputedBorder largestLeftBorder = borders.leftBorder;
    for (int y = 0; y < table.height(); y++) {
      for (int z = 0; table.cell(0, y, z) != null; z++) {
        TableComputedBorders cellBorders = table.cell(0, y, z).borders();
        cellBorders.leftBorder = strongerBorder(cellBorders.leftBorder, borders.leftBorder);
        largestLeftBorder = strongerBorder(largestLeftBorder, cellBorders.leftBorder);
      }
    }

    ComputedBorder largestRightBorder = borders.rightBorder;
    for (int y = 0; y < table.height(); y++) {
      for (int z = 0; table.cell(table.width() - 1, y, z) != null; z++) {
        TableComputedBorders cellBorders = table.cell(table.width() - 1, y, z).borders();
        cellBorders.rightBorder = strongerBorder(cellBorders.rightBorder, borders.rightBorder);
        largestRightBorder = strongerBorder(largestRightBorder, cellBorders.rightBorder);
      }
    }

    EBDimensionsUtil.setComputedBorder(
      table.tableBox(),
      largestTopBorder.borderWidth(),
      largestBottomBorder.borderWidth(),
      largestLeftBorder.borderWidth(),
      largestRightBorder.borderWidth()
    );
  }

}
