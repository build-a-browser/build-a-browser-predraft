package net.buildabrowser.babbrowser.render.content.table;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;

public class TableContent implements BoxContent {

  // TODO: Don't forget to handle out-of-flow items

  private static final EventHandler EVENT_HANDLER = new TableEventHandler();

  private final TableContentPainter painter = new TableContentPainter(this);

  private final ElementBox rootBox;
  
  private SizedTable sizedTable;

  public TableContent(ElementBox rootBox) {
    this.rootBox = rootBox;
  }

  @Override
  public void fixupChildren() {
    TableFixup.adjustTableBox(rootBox);
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    Table table = Table.create(rootBox);
    TableFormer.formTable(table, rootBox);
    table.createColumns();

    if (table.width() == 0) {
      this.sizedTable = new SizedTable(table, new float[0]);
      return new UnmanagedBoxFragment(
        LayoutUtil.constraintOrDim(widthConstraint, 0),
        LayoutUtil.constraintOrDim(heightConstraint, 0),
        0, 0,
        rootBox, painter);
    }

    // TODO: Need to merge unspecified columns with only spans

    float gridMin = TableSizeUtil.sumMinWidths(table.columns());
    float gridMax = TableSizeUtil.sumMaxWidths(table.columns());

    if (widthConstraint.isPreLayoutConstraint()) {
      float usedWidth = widthConstraint.type().equals(LayoutConstraintType.MAX_CONTENT) ?
        gridMax : gridMin;
      return new UnmanagedBoxFragment(
        LayoutUtil.constraintOrDim(widthConstraint, usedWidth),
        LayoutUtil.constraintOrDim(heightConstraint, 0),
        usedWidth, 0,
        rootBox, painter);
    }

    if (widthConstraint.value() < gridMin) {
      widthConstraint = LayoutConstraint.of(gridMin);
    }

    TableColumnSizerAuto.assignTableWidths(widthConstraint, table.columns());

    float[] rowHeights = new float[table.height()];
    layoutCellsAndHeights(table, rowHeights);
    // TODO: Respect alignments and explicit row heights
    positionCells(table, rowHeights);

    this.sizedTable = new SizedTable(table, rowHeights);
    float totalHeight = TableSizeUtil.sumSizes(rowHeights);
    float inkWidth = widthConstraint.isBounded() ?
      Math.max(widthConstraint.floatValue(), gridMin) :
      gridMax;
    return new UnmanagedBoxFragment(
      LayoutUtil.constraintOrDim(widthConstraint, gridMax),
      LayoutUtil.constraintOrDim(heightConstraint, totalHeight),
      inkWidth, totalHeight,
      rootBox, painter);
  }

  @Override
  public EventHandler eventHandler() {
    return EVENT_HANDLER;
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    // TODO: Implement this
  }

  public SizedTable sizedTable() {
    return this.sizedTable;
  }

  private void layoutCellsAndHeights(Table table, float[] rowHeights) {
    for (int x = 0; x < table.width(); x++) {
      for (int y = 0; y < table.height(); y++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() != null) continue;

          UnmanagedBoxFragment fragment = cell.cellBox().layout(
            LayoutConstraint.of(cellWidth(table, cell)), LayoutConstraint.AUTO);
          cell.setRelatedFragment(fragment);

          LayoutConstraint fragmentHeight = SizingHeightUtil.evaluateAdjustedHeightSize(
            LayoutConstraint.AUTO, cell.cellBox());

          float usedHeight = fragmentHeight.isBounded() ?
            fragmentHeight.value() :
            cell.getRelatedFragment().height(Measurement.CONTENT);
          // cell.height() > 1 is technically unspecified behaviour
          float itemHeight = usedHeight / cell.height();
          rowHeights[y] = Math.max(rowHeights[y], itemHeight);
        }
      }
    }
  }

  private float cellWidth(Table table, TableCell cell) {
    float totalWidth = 0;
    for (int x = cell.cellX(); x < cell.cellX() + cell.width(); x++) {
      totalWidth += table.column(x).usedWidth();
    }
    return totalWidth;
  }

  private List<UnmanagedBoxFragment> positionCells(Table table, float[] rowHeights) {
    List<UnmanagedBoxFragment> fragments = new ArrayList<>();

    float currentX = 0;
    for (int x = 0; x < table.width(); x++) {
      TableColumn column = table.column(x);

      float currentY = 0;
      for (int y = 0; y < table.height(); y++) {
        for (int z = 0; table.cell(x, y, z) != null; z++) {
          TableCell cell = table.cell(x, y, z);
          if (cell.getRelatedFragment() == null) continue;
          if (cell.cellX() != x || cell.cellY() != y) continue;
          
          cell.getRelatedFragment().setPos(currentX, currentY);
        }
        currentY += rowHeights[y];
      }
      currentX += column.usedWidth();
    }

    return fragments;
  }

  public static record SizedTable(Table table, float[] columnHeights) {}
  
}
