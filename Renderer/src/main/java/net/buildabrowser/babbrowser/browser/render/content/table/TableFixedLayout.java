package net.buildabrowser.babbrowser.browser.render.content.table;

import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.content.table.Table.Cell;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;

public final class TableFixedLayout {
  
  // TODO: Account for borders
  // TODO: Handle overlapping cells

  public static float[] computeColumnWidths(LayoutContext layoutContext, Table table, LayoutConstraint widthConstraint) {
    int numUnsizedItems = 0;
    float usedWidth = 0;
    float[] columnWidths = new float[table.width()];
    for (int i = 0; i < table.width();) {
      // TODO: Check column widths
      Cell cell = table.getCell(i, 0, 0);
      LayoutConstraint itemWidth = cell == null ? null : SizingUtil.evaluateBaseSize(
        layoutContext, widthConstraint,
        cell.cellBox().activeStyles().getProperty(CSSProperty.WIDTH));

      int cellWidth = cell == null ? 1 : cell.width();
      float itemWidthPer = cell == null || !itemWidth.type().equals(LayoutConstraintType.BOUNDED) ?
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
      widthConstraint.type().equals(LayoutConstraintType.BOUNDED)
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
