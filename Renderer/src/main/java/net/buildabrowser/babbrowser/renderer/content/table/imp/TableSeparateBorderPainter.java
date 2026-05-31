package net.buildabrowser.babbrowser.renderer.content.table.imp;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.BorderUtil;
import net.buildabrowser.babbrowser.renderer.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableBorderPainter;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;

public class TableSeparateBorderPainter implements TableBorderPainter {

  @Override
  public void assignBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    BorderUtil.computeBorder(table.tableBox());
    PaddingUtil.computePadding(table.tableBox(), referenceConstraint);
    TableCellUtil.forEachCell(table, cell -> assignBorders(cell));
  }

  @Override
  public void paintCellBorders(
    PaintCanvas canvas,
    Table table,
    TableCell cell,
    UnmanagedBoxFragment childFragment
  ) {
    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(
      childFragment.posX(Measurement.BORDER),
      childFragment.posY(Measurement.BORDER)));
    ElementBorderPainter.paintBorders(
      canvas, childFragment,
      TableCellUtil.outerCellWidth(table, cell),
      TableCellUtil.outerCellHeight(table, cell));
    ElementBackgroundPainter.paintDebugOutlines(canvas, childFragment);
    canvas.popPaint();
  }

  private void assignBorders(TableCell cell) {
    TableComputedBorders borders = cell.borders();
    ElementBox cellBox = cell.cellBox();
    borders.topBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.TOP, false);
    borders.bottomBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.BOTTOM, false);
    borders.leftBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.LEFT, false);
    borders.rightBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.RIGHT, false);

    cell.cellBox().dimensions().setComputedBorder(
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

}
