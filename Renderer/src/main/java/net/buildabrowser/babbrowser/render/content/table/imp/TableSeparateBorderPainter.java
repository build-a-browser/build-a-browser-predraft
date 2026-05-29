package net.buildabrowser.babbrowser.render.content.table.imp;

import java.util.Set;

import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class TableSeparateBorderPainter implements TableBorderPainter {

  @Override
  public Set<ComputedBorder> assignBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    TableCellUtil.forEachCell(table, cell -> assignBorders(cell, referenceConstraint));

    return Set.of();
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
    borders.topBorder = TableComputedBorders.computeBorder(cell, BorderSide.TOP, referenceConstraint, false);
    borders.bottomBorder = TableComputedBorders.computeBorder(cell, BorderSide.BOTTOM, referenceConstraint, false);
    borders.leftBorder = TableComputedBorders.computeBorder(cell, BorderSide.LEFT, referenceConstraint, false);
    borders.rightBorder = TableComputedBorders.computeBorder(cell, BorderSide.RIGHT, referenceConstraint, false);

    cell.cellBox().dimensions().setComputedBorder(
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

}
