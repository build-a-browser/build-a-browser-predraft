package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundImagePainter;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.render.content.table.Table.ColumnGroup;
import net.buildabrowser.babbrowser.render.content.table.Table.RowGroup;
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
    paintColumnGroups(canvas, vpIntersection);
    paintColumns(canvas, vpIntersection);
    paintRowGroups(canvas, vpIntersection);
    paintRows(canvas, vpIntersection);
    paintCells(canvas, vpIntersection);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    // TODO: Adjust box to exclude captions
    ElementBackgroundPainter.paintBackground(canvas, fragment);
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
    for (int x = 0; x < table.width(); x++) {
      float columnWidth = table.column(x).usedWidth();
      for (int y = 0; y < table.height(); y++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          paintCell(canvas, cell, x, y, vpIntersection, columnWidth);
        }
      }
    }
  }

  private void paintCell(
    PaintCanvas canvas,
    TableCell cell,
    int x, int y,
    int[] vpIntersection,
    float columnWidth
  ) {
    UnmanagedBoxFragment childFragment = cell.getRelatedFragment();
    if (childFragment == null)
      return;
    if (cell.cellX() != x || cell.cellY() != y)
      return;

    paintFragmentBackground(canvas, childFragment);
    
    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(childFragment.posX(Measurement.CONTENT), childFragment.posY(Measurement.CONTENT)));
    // TODO: Skip if context differs
    PaintUtil.maybePaintFragment(
      childFragment, canvas, vpIntersection,
      childFragment.painter()::paintBackground);
    PaintUtil.maybePaintFragment(
      childFragment, canvas, vpIntersection,
      childFragment.painter()::paint);
    canvas.popPaint();
  }

  private void paintFragmentBackground(PaintCanvas canvas, BoxFragment fragment) {
    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(fragment.posX(Measurement.BORDER), fragment.posY(Measurement.BORDER)));
    ElementBackgroundImagePainter.paintBackgroundImages(canvas, fragment);
    canvas.popPaint();
  }
  
}
