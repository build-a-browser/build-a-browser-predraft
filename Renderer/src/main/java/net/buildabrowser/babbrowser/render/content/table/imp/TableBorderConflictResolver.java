package net.buildabrowser.babbrowser.render.content.table.imp;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.content.table.TableConflictOps;

public final class TableBorderConflictResolver {

  private static final TableConflictOps RIGHT_CONFLICT_OPS = new TableRightConflictOps();
  private static final TableConflictOps BOTTOM_CONFLICT_OPS = new TableBottomConflictOps();

  private static final List<CSSValue> BORDER_STYLE_ORDER = List.of(
    BorderStyleValue.DOUBLE,
    BorderStyleValue.SOLID,
    BorderStyleValue.DASHED,
    BorderStyleValue.DOTTED,
    BorderStyleValue.RIDGE,
    BorderStyleValue.OUTSET,
    BorderStyleValue.GROOVE,
    BorderStyleValue.INSET,
    CSSValue.NONE
  );
  
  private TableBorderConflictResolver() {}

  public static void resolveBorderConflicts(Table table) {
    TableCellUtil.forEachCell(table, cell -> {
      resolveBorderConflicts(RIGHT_CONFLICT_OPS, table, cell);
      resolveBorderConflicts(BOTTOM_CONFLICT_OPS, table, cell);
    });
  }

  public static int compareCellOrder(TableCell a, TableCell b) {
    return
      a.cellY() < b.cellY() ? -1 :
      a.cellY() > b.cellY() ? 1 :
      a.cellX() < b.cellX() ? -1 :
      a.cellX() > b.cellX() ? 1 :
      0;
  }

  public static boolean isCurrentMoreSpecific(
    ComputedBorder oldBorder, ComputedBorder currentBorder
  ) {
    if (
      oldBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
      && !currentBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
    ) return false;
    if (
      !oldBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
      && currentBorder.borderStyle().equals(BorderStyleValue.HIDDEN)
    ) return true;

    if (
      oldBorder.borderWidth() > currentBorder.borderWidth()
    ) return false;
    if (
      oldBorder.borderWidth() < currentBorder.borderWidth()
    ) return true;

    int oldBorderOrder = BORDER_STYLE_ORDER.indexOf(oldBorder.borderStyle());
    int currentBorderOrder = BORDER_STYLE_ORDER.indexOf(currentBorder.borderStyle());

    if (oldBorderOrder < currentBorderOrder) return false;
    if (oldBorderOrder > currentBorderOrder) return true;

    return false;
  }
  
  // TODO: There is no way this is performant
  // (but tables are uncommon enough for this to be left for later...)
  private static void resolveBorderConflicts(
    TableConflictOps conflictOps,
    Table table, TableCell cell
  ) {
    Set<TableCell> cellSet1 = new TreeSet<>(TableBorderConflictResolver::compareCellOrder);
    Set<TableCell> cellSet2 = new TreeSet<>(TableBorderConflictResolver::compareCellOrder);

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

}
