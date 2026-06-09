package net.buildabrowser.babbrowser.renderer.content.table.imp.border;

import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.BorderUtil;
import net.buildabrowser.babbrowser.renderer.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class TableSeparateBorderAssigner {

  private TableSeparateBorderAssigner() {}

  public static TableBorderAssignment assignBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    BorderUtil.computeBorder(table.tableBox());
    PaddingUtil.computePadding(table.tableBox(), referenceConstraint);
    TableCellUtil.forEachCell(table, cell -> assignBorders(cell));

    return null;
  }

  private static void assignBorders(TableCell cell) {
    TableComputedBorders borders = cell.borders();
    ElementBox cellBox = cell.cellBox();
    borders.topBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.TOP, false);
    borders.bottomBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.BOTTOM, false);
    borders.leftBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.LEFT, false);
    borders.rightBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.RIGHT, false);

    EBDimensionsUtil.setComputedBorder(
      cell.cellBox(),
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

}
