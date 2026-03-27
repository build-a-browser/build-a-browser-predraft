package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.render.content.table.Table.Cell;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public final class TableFixedLayout {
  
  // TODO: Account for borders
  // TODO: Handle overlapping cells

  public static float[] computeColumnWidths(Table table, LayoutConstraint widthConstraint) {
    int numUnsizedItems = 0;
    float usedWidth = 0;
    float[] columnWidths = new float[table.width()];
    for (int i = 0; i < table.width();) {
      // TODO: Check column widths
      Cell cell = table.getCell(i, 0, 0);
      LayoutConstraint itemWidth = cell == null ? null :
        SizingWidthUtil.clampWidth(widthConstraint, cell.cellBox(),
          SizingWidthUtil.evaluateAdjustedWidthSize(
            widthConstraint, cell.cellBox()));

      int cellWidth = cell == null ? 1 : cell.width();
      float itemWidthPer = cell == null || !itemWidth.isBounded() ?
        -1 :
        itemWidth.value() / cellWidth;
      
      for (int j = 0; j < cellWidth; j++) {
        columnWidths[i + j] = itemWidthPer;
      }
      if (itemWidthPer == -1) {
        numUnsizedItems += cellWidth;
      } else {
        usedWidth += itemWidth.value();
      }
      i += cellWidth;
    }

    boolean canDiv =
      widthConstraint.isBounded()
      && widthConstraint.value() > usedWidth;

    float divWidth = canDiv ? (widthConstraint.value() - usedWidth) / numUnsizedItems : 0;
    for (int i = 0; i < table.width(); i++) {
      if (columnWidths[i] == -1) {
        columnWidths[i] = divWidth;
      }
    }

    return columnWidths;
  }

}
