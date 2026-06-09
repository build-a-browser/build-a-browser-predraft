package net.buildabrowser.babbrowser.renderer.paint.painters.table;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public interface TableBorderPainter {

  default void paintCellBorders(
    PaintCanvas canvas,
    Table table,
    TableCell cell,
    UnmanagedBoxFragment<?> childFragment
  ) {}

  default void paintSavedBorders(
    PaintCanvas canvas,
    TableBorderAssignment assignment,
    Table table
  ) {}

}
