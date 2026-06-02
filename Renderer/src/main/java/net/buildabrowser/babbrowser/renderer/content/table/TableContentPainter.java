package net.buildabrowser.babbrowser.renderer.content.table;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBackgroundImagePainter;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.renderer.content.table.Table.ColumnGroup;
import net.buildabrowser.babbrowser.renderer.content.table.Table.RowGroup;
import net.buildabrowser.babbrowser.renderer.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class TableContentPainter implements BoxPainter {

  private final TableContent content;

  public TableContentPainter(TableContent content) {
    this.content = content;
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    if (content.borderPainter() == null) return;
    paintColumnGroups(canvas, vpIntersection);
    paintColumns(canvas, vpIntersection);
    paintRowGroups(canvas, vpIntersection);
    paintRows(canvas, vpIntersection);
    paintCells(canvas, vpIntersection);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    // TODO: Adjust box to exclude captions
    ElementBackgroundImagePainter.paintBackgroundImages(
      canvas, fragment,
      fragment.width(Measurement.BORDER),
      fragment.height(Measurement.BORDER));

    ElementBorderPainter.paintBorders(canvas, fragment);
    ElementBackgroundPainter.paintDebugOutlines(canvas, fragment);
  }

  private void paintColumnGroups(PaintCanvas canvas, VpIntersection vpIntersection) {
    Table table = content.table();
    for (ColumnGroup colGroup: table.columnGroups()) {
      BoxFragment fragment = colGroup.groupBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintColumns(PaintCanvas canvas, VpIntersection vpIntersection) {
    Table table = content.table();
    for (TableColumn column: table.columns()) {
      BoxFragment fragment = column.columnBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintRowGroups(PaintCanvas canvas, VpIntersection vpIntersection) {
    Table table = content.table();
    for (RowGroup rowGroup: table.rowGroups()) {
      BoxFragment fragment = rowGroup.groupBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintRows(PaintCanvas canvas, VpIntersection vpIntersection) {
    Table table = content.table();
    for (TableRow row: table.rows()) {
      BoxFragment fragment = row.rowBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintCells(PaintCanvas canvas, VpIntersection vpIntersection) {
    Table table = content.table();
    TableCellUtil.forEachCell(table, cell -> {
      UnmanagedBoxFragment childFragment = cell.getRelatedFragment();
      if (childFragment == null) return;
      paintCellBackground(canvas, table, cell, childFragment);
      content.borderPainter().paintCellBorders(canvas, table, cell, childFragment);
      // TODO: Also paint outline
    });

    content.borderPainter().paintSavedBorders(canvas, table);

    TableCellUtil.forEachCell(table, cell -> {
      UnmanagedBoxFragment childFragment = cell.getRelatedFragment();
      if (childFragment == null) return;
      // TODO: Skip if context differs
      canvas.withTransform(
        t -> t.translate(
          childFragment.posX(Measurement.CONTENT),
          childFragment.posY(Measurement.CONTENT)),
        c -> PaintUtil.maybePaintFragment(
          childFragment, c, vpIntersection,
          childFragment.painter()::paint));
    });
  }

  private void paintFragmentBackground(
    PaintCanvas canvas,
    BoxFragment fragment
  ) {
    canvas.withTransform(
      t -> t.translate(
        fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.BORDER)),
      c -> ElementBackgroundImagePainter.paintBackgroundImages(
        c, fragment,
        fragment.width(Measurement.BORDER),
        fragment.height(Measurement.BORDER)));
  }

  private void paintCellBackground(
    PaintCanvas canvas,
    Table table,
    TableCell cell,
    UnmanagedBoxFragment fragment
  ) {
    // TODO: Do we or do we not need to call the actual fragment's paintBackground
    canvas.withTransform(
      t -> t.translate(
        fragment.posX(Measurement.BORDER),
        fragment.posY(Measurement.BORDER)),
      c -> ElementBackgroundImagePainter.paintBackgroundImages(
        c, fragment,
        TableCellUtil.outerCellWidth(table, cell),
        TableCellUtil.outerCellHeight(table, cell)));
  }
  
}
