package net.buildabrowser.babbrowser.renderer.paint.painters.table;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBorderPainter;

public class TableSeparateBorderPainter implements TableBorderPainter {

  @Override
  public void paintCellBorders(
    PaintCanvas canvas,
    Table table,
    TableCell cell,
    UnmanagedBoxFragment<?> childFragment
  ) {
    canvas.withTransform(
      t -> t.translate(
        childFragment.posX(Measurement.BORDER),
        childFragment.posY(Measurement.BORDER)),
      c -> {
        ElementBorderPainter.paintBorders(
          canvas, childFragment,
          TableCellUtil.outerCellWidth(table, cell),
          TableCellUtil.outerCellHeight(table, cell));
        ElementBackgroundPainter.paintDebugOutlines(canvas, childFragment);
      });
  }

}
