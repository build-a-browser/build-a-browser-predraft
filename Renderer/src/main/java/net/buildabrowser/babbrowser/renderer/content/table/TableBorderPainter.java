package net.buildabrowser.babbrowser.renderer.content.table;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

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
