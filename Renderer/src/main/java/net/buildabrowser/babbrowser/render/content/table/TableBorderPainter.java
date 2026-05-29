package net.buildabrowser.babbrowser.render.content.table;

import java.util.Set;

import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public interface TableBorderPainter {
  
  Set<ComputedBorder> assignBorders(
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
    Table table,
    Set<ComputedBorder> borderOrder
  ) {}

}
