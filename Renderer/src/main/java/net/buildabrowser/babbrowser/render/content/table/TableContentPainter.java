package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundImagePainter;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.Table.ColumnGroup;
import net.buildabrowser.babbrowser.render.content.table.Table.RowGroup;
import net.buildabrowser.babbrowser.render.content.table.imp.TableCellUtil;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.PaintUtil;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class TableContentPainter implements BoxPainter {

  private final TableContent content;

  public TableContentPainter(TableContent content) {
    this.content = content;
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    if (content.borderPainter() == null) return;
    paintColumnGroups(canvas, vpIntersection);
    paintColumns(canvas, vpIntersection);
    paintRowGroups(canvas, vpIntersection);
    paintRows(canvas, vpIntersection);
    paintCells(canvas, vpIntersection);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    // TODO: Adjust box to exclude captions
    ElementBackgroundImagePainter.paintBackgroundImages(
      canvas, fragment,
      fragment.width(Measurement.BORDER),
      fragment.height(Measurement.BORDER));

    ElementBorderPainter.paintBorders(canvas, fragment);
    ElementBackgroundPainter.paintDebugOutlines(canvas, fragment);
  }

  private void paintColumnGroups(PaintCanvas canvas, int[] vpIntersection) {
    Table table = content.table();
    for (ColumnGroup colGroup: table.columnGroups()) {
      BoxFragment fragment = colGroup.groupBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintColumns(PaintCanvas canvas, int[] vpIntersection) {
    Table table = content.table();
    for (TableColumn column: table.columns()) {
      BoxFragment fragment = column.columnBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintRowGroups(PaintCanvas canvas, int[] vpIntersection) {
    Table table = content.table();
    for (RowGroup rowGroup: table.rowGroups()) {
      BoxFragment fragment = rowGroup.groupBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintRows(PaintCanvas canvas, int[] vpIntersection) {
    Table table = content.table();
    for (TableRow row: table.rows()) {
      BoxFragment fragment = row.rowBox().positioningFragment();
      paintFragmentBackground(canvas, fragment);
    }
  }

  private void paintCells(PaintCanvas canvas, int[] vpIntersection) {
    Table table = content.table();
    TableCellUtil.forEachCell(table, cell -> {
      UnmanagedBoxFragment childFragment = cell.getRelatedFragment();
      if (childFragment == null) return;
      canvas.pushPaint();
      paintCellBackground(canvas, table, cell, childFragment);
      content.borderPainter().paintCellBorders(canvas, table, cell, childFragment);
      // TODO: Also paint outline
      canvas.popPaint();
    });

    content.borderPainter().paintSavedBorders(canvas, table);

    TableCellUtil.forEachCell(table, cell -> {
      UnmanagedBoxFragment childFragment = cell.getRelatedFragment();
      if (childFragment == null) return;
      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(
        childFragment.posX(Measurement.CONTENT),
        childFragment.posY(Measurement.CONTENT)));
      // TODO: Skip if context differs
      PaintUtil.maybePaintFragment(
        childFragment, canvas, vpIntersection,
        childFragment.painter()::paint);
      canvas.popPaint();
    });
  }

  private void paintFragmentBackground(
    PaintCanvas canvas,
    BoxFragment fragment
  ) {
    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(fragment.posX(Measurement.BORDER), fragment.posY(Measurement.BORDER)));
    ElementBackgroundImagePainter.paintBackgroundImages(
      canvas, fragment,
      fragment.width(Measurement.BORDER),
      fragment.height(Measurement.BORDER));
    canvas.popPaint();
  }

  private void paintCellBackground(
    PaintCanvas canvas,
    Table table,
    TableCell cell,
    UnmanagedBoxFragment fragment
  ) {
    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(fragment.posX(Measurement.BORDER), fragment.posY(Measurement.BORDER)));
    // TODO: Do we or do we not need to call the actual fragment's paintBackground
    ElementBackgroundImagePainter.paintBackgroundImages(
      canvas, fragment,
      TableCellUtil.outerCellWidth(table, cell),
      TableCellUtil.outerCellHeight(table, cell));
    canvas.popPaint();
  }
  
}
