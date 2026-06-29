package net.buildabrowser.babbrowser.renderer.paint.painters.table;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.Table.ColumnGroup;
import net.buildabrowser.babbrowser.renderer.content.table.Table.RowGroup;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableColumn;
import net.buildabrowser.babbrowser.renderer.content.table.TableRow;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.table.TableBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBorderPainter;

public class TableBoxPainter implements BoxPainter<TableBoxFragment> {

  private static final TableBorderPainter SEPARATE_PAINTER = new TableSeparateBorderPainter();
  private static final TableBorderPainter COLLAPSED_PAINTER = new TableCollapsedBorderPainter();

  @Override
  public void paint(
    TableBoxFragment tableFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    paintColumnGroups(tableFragment, canvas, vpIntersection);
    paintColumns(tableFragment, canvas, vpIntersection);
    paintRowGroups(tableFragment, canvas, vpIntersection);
    paintRows(tableFragment, canvas, vpIntersection);
    paintCells(tableFragment, canvas, vpIntersection);
  }

  @Override
  public void paintBackground(TableBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    // TODO: Adjust box to exclude captions
    float fragmentWidth = Math.max(0, fragment.width(Measurement.BORDER));
    float fragmentHeight = Math.max(0, fragment.height(Measurement.BORDER));

    ElementBackgroundPainter.paintBackgroundImages(
      canvas, fragment, vpIntersection,
      fragmentWidth, fragmentHeight);

    ElementBorderPainter.paintBorders(
      canvas, fragment,
      fragmentWidth, fragmentHeight);
    ElementBackgroundPainter.paintDebugOutlines(
      canvas, fragmentWidth, fragmentHeight);
  }

  private void paintColumnGroups(
    TableBoxFragment tableFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    Table table = tableFragment.table();
    for (ColumnGroup colGroup: table.columnGroups()) {
      BoxFragment<?> colGroupFragment = colGroup.groupBox().positioningFragment();
      paintFragmentBackground(canvas, colGroupFragment, vpIntersection);
    }
  }

  private void paintColumns(
    TableBoxFragment tableFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    Table table = tableFragment.table();
    for (TableColumn column: table.columns()) {
      BoxFragment<?> columnFragment = column.columnBox().positioningFragment();
      paintFragmentBackground(canvas, columnFragment, vpIntersection);
    }
  }

  private void paintRowGroups(
    TableBoxFragment tableFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    Table table = tableFragment.table();
    for (RowGroup rowGroup: table.rowGroups()) {
      BoxFragment<?> rowGroupFragment = rowGroup.groupBox().positioningFragment();
      paintFragmentBackground(canvas, rowGroupFragment, vpIntersection);
    }
  }

  private void paintRows(
    TableBoxFragment tableFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    Table table = tableFragment.table();
    for (TableRow row: table.rows()) {
      BoxFragment<?> rowFragment = row.rowBox().positioningFragment();
      paintFragmentBackground(canvas, rowFragment, vpIntersection);
    }
  }

  private void paintCells(
    TableBoxFragment tableFragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    StackingContext refContext = tableFragment.box().stackingContext();
    TableBorderPainter borderPainter = tableFragment.borderAssignment() == null ?
      SEPARATE_PAINTER : COLLAPSED_PAINTER;

    Table table = tableFragment.table();
    TableCellUtil.forEachCell(table, cell -> {
      UnmanagedBoxFragment<?> cellFragment = cell.getRelatedFragment();
      if (cellFragment == null) return;
      if (cellFragment.box().stackingContext() != refContext) return;
      paintCellBackground(canvas, cellFragment, vpIntersection, table, cell);
      borderPainter.paintCellBorders(canvas, table, cell, cellFragment);
      // TODO: Also paint outline
    });

    borderPainter.paintSavedBorders(
      canvas, tableFragment.borderAssignment(), table);

    TableCellUtil.forEachCell(table, cell -> {
      UnmanagedBoxFragment<?> cellFragment = cell.getRelatedFragment();
      if (cellFragment == null) return;
      if (cellFragment.box().stackingContext() != refContext) return;
      // TODO: Skip if context differs
      canvas.withTransform(
        t -> t.translate(
          cellFragment.posX(Measurement.CONTENT),
          cellFragment.posY(Measurement.CONTENT)),
        c -> PaintUtil.maybePaintFragment(
          cellFragment, c, vpIntersection,
          (f, c2, vpi) -> f.withPainterV((p, f2) -> p.paint(f2, c, vpi))));
    });
  }

  private void paintFragmentBackground(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection
  ) {
    canvas.withTransform(
      t -> t.translate(
        fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.BORDER)),
      c -> ElementBackgroundPainter.paintBackgroundImages(
        c, fragment, vpIntersection,
        fragment.width(Measurement.BORDER),
        fragment.height(Measurement.BORDER)));
  }

  private void paintCellBackground(
    PaintCanvas canvas,
    UnmanagedBoxFragment<?> fragment,
    VpIntersection vpIntersection,
    Table table,
    TableCell cell
  ) {
    // TODO: Do we or do we not need to call the actual fragment's paintBackground
    canvas.withTransform(
      t -> t.translate(
        fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.BORDER)),
      c -> ElementBackgroundPainter.paintBackgroundImages(
        c, fragment, vpIntersection,
        TableCellUtil.outerCellWidth(table, cell),
        TableCellUtil.outerCellHeight(table, cell)));
  }
  
}
