package net.buildabrowser.babbrowser.renderer.content.table.imp.border;

import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.computeInitialBorders;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssignerUtil.isCurrentMoreSpecific;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderCellAssigner.assignCellBorders;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderTrackTableAssigner.assignColumnsBorders;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderTrackTableAssigner.assignRowsBorders;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderTrackTableAssigner.assignTableBorders;

import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;

// NOSPEC: The spec way to collapse borders does not seem to match major browsers
// or account for track/track-group borders, so I instead am using an approach that
// seems to better mirror real browsers
public final class TableCollapsedBorderAssigner {

  private TableCollapsedBorderAssigner() {}

  public static TableBorderAssignment assignBorders(Table table) {
    TableComputedBorders[][] slotGrid = populateSlotGrid(table);
    Set<SlotComputedBorder> borderOrder = new TreeSet<>((a, b) -> compareSlotBorderOrder(a, b));

    assignColumnsBorders(slotGrid, table);
    assignRowsBorders(slotGrid, table);
    TableCellUtil.forEachCell(
      table, cell -> assignCellBorders(slotGrid, table, cell));
    // This part is kind of from the spec, but taking in 
    // Solely affects layout, not painting
    computeInitialBorders(table.tableBox(), table.borders());
    TableBorderConflictResolver.resolveBorderConflicts(table);
    // Called after, not before, because TableBorderConflictResolver has its own table handling
    assignTableBorders(slotGrid, table);
    orderSlotGrid(slotGrid, table, borderOrder);

    TableCellUtil.forEachCell(table, c -> assignFinalDimensions(c));

    return new TableBorderAssignment(slotGrid, borderOrder);
  }

  private static void assignFinalDimensions(TableCell cell) {
    TableComputedBorders borders = cell.borders();
    EBDimensionsUtil.setComputedBorder(
      cell.cellBox(),
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

  private static TableComputedBorders[][] populateSlotGrid(Table table) {
    TableComputedBorders[][] slotGrid = new TableComputedBorders[table.height()][table.width()];
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        slotGrid[y][x] = new TableComputedBorders();
      }
    }

    return slotGrid;
  }

  private static void orderSlotGrid(
    TableComputedBorders[][] slotGrid, Table table,
    Set<SlotComputedBorder> borderOrder
  ) {
    borderOrder.clear();
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        TableComputedBorders slot = slotGrid[y][x];
        if (slot.topBorder != null) {
          borderOrder.add(new SlotComputedBorder(x, y, slot.topBorder));
        }
        if (slot.bottomBorder != null) {
          borderOrder.add(new SlotComputedBorder(x, y, slot.bottomBorder));
        }
        if (slot.leftBorder != null) {
          borderOrder.add(new SlotComputedBorder(x, y, slot.leftBorder));
        }
        if (slot.rightBorder != null) {
          borderOrder.add(new SlotComputedBorder(x, y, slot.rightBorder));
        }
      }
    }
  }

  private static int compareSlotBorderOrder(SlotComputedBorder a, SlotComputedBorder b) {
    int order = compareBorderOrder(a.border(), b.border());
    if (order != 0) return order;
    
    int xCompare = Integer.compare(a.x(), b.x());
    if (xCompare != 0) return xCompare;
    
    return Integer.compare(a.y(), b.y());
  }

  // Specific-Most, then Bottom-Right-most, come last
  private static int compareBorderOrder(ComputedBorder a, ComputedBorder b) {
    if (isCurrentMoreSpecific(b, a)) {
      return 1;
    } else if (isCurrentMoreSpecific(a, b)) {
      return -1;
    }
    
    return Integer.compare(b.sourceSide().ordinal(), a.sourceSide().ordinal());
  }

  public static record SlotComputedBorder(
    int x, int y, ComputedBorder border
  ) {}

}
