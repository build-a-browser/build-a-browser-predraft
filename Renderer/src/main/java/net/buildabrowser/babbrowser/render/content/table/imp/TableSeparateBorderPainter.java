package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.BorderUtil;
import net.buildabrowser.babbrowser.render.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class TableSeparateBorderPainter implements TableBorderPainter {

  @Override
  public void assignBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    BorderUtil.computeBorder(table.tableBox());
    PaddingUtil.computePadding(table.tableBox(), referenceConstraint);
    TableCellUtil.forEachCell(table, cell -> assignBorders(cell, referenceConstraint));
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

  private void assignBorders(
    TableCell cell,
    LayoutConstraint referenceConstraint
  ) {
    TableComputedBorders borders = cell.borders();
    ElementBox cellBox = cell.cellBox();
    borders.topBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.TOP, referenceConstraint, false);
    borders.bottomBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.BOTTOM, referenceConstraint, false);
    borders.leftBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.LEFT, referenceConstraint, false);
    borders.rightBorder = TableComputedBorders.computeBorder(cellBox, BorderSide.RIGHT, referenceConstraint, false);

    cell.cellBox().dimensions().setComputedBorder(
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

}
