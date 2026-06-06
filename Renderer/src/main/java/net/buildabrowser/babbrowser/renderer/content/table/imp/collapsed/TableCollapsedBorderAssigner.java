package net.buildabrowser.babbrowser.renderer.content.table.imp.collapsed;

import static net.buildabrowser.babbrowser.renderer.content.table.imp.collapsed.TableCollapsedBorderAssignerUtil.computeInitialBorders;
import static net.buildabrowser.babbrowser.renderer.content.table.imp.collapsed.TableCollapsedBorderAssignerUtil.isCurrentMoreSpecific;

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
public class TableCollapsedBorderAssigner {

  private final TableCollapsedBorderTrackTableAssigner trackAssigner = new TableCollapsedBorderTrackTableAssigner(this);
  private final TableCollapsedBorderCellAssigner cellAssigner = new TableCollapsedBorderCellAssigner(this);
  private final Set<SlotComputedBorder> borderOrder = new TreeSet<>(this::compareSlotBorderOrder);

  private TableComputedBorders[][] slotGrid;

  public void assignBorders(Table table) {
    populateSlotGrid(table);
    trackAssigner.assignColumnsBorders(table);
    trackAssigner.assignRowsBorders(table);
    TableCellUtil.forEachCell(table, cell -> cellAssigner.assignCellBorders(table, cell));
    // This part is kind of from the spec, but taking in 
    // Solely affects layout, not painting
    computeInitialBorders(table.tableBox(), table.borders());
    TableBorderConflictResolver.resolveBorderConflicts(table);
    // Called after, not before, because TableBorderConflictResolver has its own table handling
    trackAssigner.assignTableBorders(table);
    orderSlotGrid(table);

    TableCellUtil.forEachCell(table, this::assignFinalDimensions);
  }

  Set<SlotComputedBorder> borderOrder() {
    return this.borderOrder;
  }

  TableComputedBorders[][] slotGrid() {
    return this.slotGrid;
  }

  private void assignFinalDimensions(TableCell cell) {
    TableComputedBorders borders = cell.borders();
    EBDimensionsUtil.setComputedBorder(
      cell.cellBox(),
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

  private void populateSlotGrid(Table table) {
    this.slotGrid = new TableComputedBorders[table.height()][table.width()];
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        slotGrid[y][x] = new TableComputedBorders();
      }
    }
  }

  private void orderSlotGrid(Table table) {
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

  private int compareSlotBorderOrder(SlotComputedBorder a, SlotComputedBorder b) {
    int order = compareBorderOrder(a.border(), b.border());
    if (order != 0) return order;
    
    int xCompare = Integer.compare(a.x(), b.x());
    if (xCompare != 0) return xCompare;
    
    return Integer.compare(a.y(), b.y());
  }

  // Specific-Most, then Bottom-Right-most, come last
  private int compareBorderOrder(ComputedBorder a, ComputedBorder b) {
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
