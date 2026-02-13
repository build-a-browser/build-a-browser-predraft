package net.buildabrowser.babbrowser.browser.render.content.table;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.table.Table.Cell;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class TableContentPainter implements BoxPainter {

  private final TableContent content;

  public TableContentPainter(TableContent content) {
    this.content = content;
  }

  // TODO: Real implementations, these are stubs

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    canvas.alterPaint(p -> p.setColor(fragment.box().activeStyles().backgroundColor()));
    canvas.drawBox(0, 0, fragment.contentWidth(), fragment.contentHeight());

    Table table = content.sizedTable().table();
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.getCell(x, y, z) != null; z++) {
          Cell cell = table.getCell(x, y, z);
          UnmanagedBoxFragment childFragment = cell.getRelatedFragment();
          if (childFragment == null) continue;
          if (cell.cellX() != x || cell.cellY() != y) continue;

          canvas.pushPaint();
          canvas.alterPaint(p -> p.incOffset(childFragment.contentX(), childFragment.contentY()));
          canvas.alterPaint(p -> p.setColor(childFragment.box().activeStyles().backgroundColor()));
          canvas.drawBox(0, 0,
            content.sizedTable().columnWidths()[x],
            content.sizedTable().columnHeights()[y]);
          // TODO: Call paintBackground?
          childFragment.painter().paint(childFragment, canvas);
          canvas.popPaint();
        }
      }
    }
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    
  }
  
}
