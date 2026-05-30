package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public interface TableBorderPainter {
  
  void assignBorders(
    Table table,
    LayoutConstraint referenceConstraint
  );

  default void paintCellBorders(
    PaintCanvas canvas,
    Table table,
    TableCell cell,
    UnmanagedBoxFragment childFragment
  ) {}

  default void paintSavedBorders(
    PaintCanvas canvas,
    Table table
  ) {}

}
