package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
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
    for (int x = 0; x < table.width(); x++) {
      for (int y = 0; y < table.height(); y++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.cellX() != x || cell.cellY() != y) continue;
          
          assignBorders(cell, referenceConstraint);
        }
      }
    }
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
      TableCellMetrics.outerCellWidth(table, cell),
      TableCellMetrics.outerCellHeight(table, cell));
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
