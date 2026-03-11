package net.buildabrowser.babbrowser.browser.render.content.table;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.table.Table.Cell;
import net.buildabrowser.babbrowser.browser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;

public class TableContent implements BoxContent {

  // TODO: Don't forget to handle out-of-flow items


  private final TableContentPainter painter = new TableContentPainter(this);

  private final ElementBox rootBox;
  
  private SizedTable sizedTable;

  public TableContent(ElementBox rootBox) {
    this.rootBox = rootBox;
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    TableFixup.adjustTableBox(rootBox);
    Table table = Table.create();
    TableFormer.formTable(table, rootBox);

    if (table.width() == 0) {
      this.sizedTable = new SizedTable(table, new float[0], new float[0]);
      return new UnmanagedBoxFragment(
        LayoutUtil.constraintOrDim(widthConstraint, 0),
        LayoutUtil.constraintOrDim(heightConstraint, 0),
        rootBox, painter);
    }

    float[] columnWidths = TableFixedLayout.computeColumnWidths(layoutContext, table, widthConstraint);
    float totalWidth = sumWidths(columnWidths);
    if (widthConstraint.isPreLayoutConstraint()) {
      return new UnmanagedBoxFragment(
        LayoutUtil.constraintOrDim(widthConstraint, totalWidth),
        LayoutUtil.constraintOrDim(heightConstraint, 0),
        rootBox, painter);  
    }

    float[] rowHeights = new float[table.height()];
    layoutCellsAndHeights(layoutContext, table, columnWidths, rowHeights);
    // TODO: Respect alignments and explicit row heights
    positionCells(layoutContext, table, columnWidths, rowHeights);

    this.sizedTable = new SizedTable(table, columnWidths, rowHeights);
    return new UnmanagedBoxFragment(
      LayoutUtil.constraintOrDim(widthConstraint, totalWidth),
      LayoutUtil.constraintOrDim(heightConstraint, sumWidths(rowHeights)),
      rootBox, painter);
  }

  public SizedTable sizedTable() {
    return this.sizedTable;
  }

  private void layoutCellsAndHeights(LayoutContext layoutContext, Table table, float[] columnWidths, float[] rowHeights) {
    for (int y = 0; y < table.height(); y++) {
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.getCell(x, y, z) != null; z++) {
          Cell cell = table.getCell(x, y, z);
          if (cell.getRelatedFragment() != null) continue;

          UnmanagedBoxFragment fragment = cell.cellBox().layout(
            layoutContext, LayoutConstraint.of(columnWidths[x]), LayoutConstraint.AUTO);
          cell.setRelatedFragment(fragment);

          LayoutConstraint fragmentHeight = SizingUtil.evaluateAdjustedHeightSize(
            layoutContext, LayoutConstraint.AUTO, cell.cellBox(),
            cell.cellBox().activeStyles().getProperty(CSSProperty.HEIGHT));

          float usedHeight = fragmentHeight.type().equals(LayoutConstraintType.BOUNDED) ?
            fragmentHeight.value() :
            cell.getRelatedFragment().contentHeight();
          // cell.height() > 1 is technically unspecified behaviour
          float itemHeight = usedHeight / cell.height();
          rowHeights[y] = Math.max(rowHeights[y], itemHeight);
        }
      }
    }
  }

  private List<UnmanagedBoxFragment> positionCells(LayoutContext layoutContext, Table table, float[] columnWidths, float[] rowHeights) {
    List<UnmanagedBoxFragment> fragments = new ArrayList<>();

    float currentY = 0;
    for (int y = 0; y < table.height(); y++) {
      float currentX = 0;
      for (int x = 0; x < table.width(); x++) {
        for (int z = 0; table.getCell(x, y, z) != null; z++) {
          Cell cell = table.getCell(x, y, z);
          if (cell.getRelatedFragment() == null) continue;
          if (cell.cellX() != x || cell.cellY() != y) continue;
          
          cell.getRelatedFragment().setPos(currentX, currentY);
        }
        currentX += columnWidths[x];
      }
      currentY += rowHeights[y];
    }

    return fragments;
  }

  private float sumWidths(float[] columnWidths) {
    float totalWidth = 0;
    for (float width: columnWidths) {
      totalWidth += width;
    }

    return totalWidth;
  }

  public static record SizedTable(Table table, float[] columnWidths, float[] columnHeights) {}

  @Override
  public void positionLayers(float layerX, float layerY, GlobalLayoutContext layoutContext) {
    // TODO: Implement this
  }
  
}
