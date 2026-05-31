package net.buildabrowser.babbrowser.renderer.content.table;

import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;

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
