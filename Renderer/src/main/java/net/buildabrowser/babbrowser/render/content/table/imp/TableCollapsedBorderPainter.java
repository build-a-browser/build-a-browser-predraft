package net.buildabrowser.babbrowser.render.content.table.imp;

import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.BorderSide;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class TableCollapsedBorderPainter implements TableBorderPainter {

  @Override
  public Set<ComputedBorder> assignBorders(Table table, LayoutConstraint referenceConstraint) {
    // NoSpec: Retain all borders, globally sorted, for painting purposes
    // Other browsers seem to behave this way
    Set<ComputedBorder> allBorders = new TreeSet<>(this::compareBorderOrder);
    TableCellUtil.forEachCell(table, cell -> assignInitialBorders(cell, referenceConstraint, allBorders));
    // Spec: Use proper conflict resolution for sizing purposes
    TableBorderConflictResolver.resolveBorderConflicts(table);
    TableCellUtil.forEachCell(table, this::assignFinalDimensions);

    return allBorders;
  }

  @Override
  public void paintSavedBorders(
    PaintCanvas canvas, Table table, Set<ComputedBorder> borderOrder
  ) {
    for (ComputedBorder border: borderOrder) {
      paintSavedBorder(canvas, table, border);
    }
  }

  private void paintSavedBorder(PaintCanvas canvas, Table table, ComputedBorder border) {    
    TableCell cell = border.sourceCell();
    BoxFragment cellFragment = cell.getRelatedFragment();

    TableComputedBorders borders = cell.preservedBorders();

    CSSValue topStyle = borders.topBorder.borderStyle();
    CSSValue bottomStyle = borders.bottomBorder.borderStyle();
    CSSValue leftStyle = borders.leftBorder.borderStyle();
    CSSValue rightStyle = borders.rightBorder.borderStyle();

    // TODO: This should be done at compute?
    float topBorderWidth = topStyle.equals(CSSValue.NONE) ? 0 : borders.topBorder.borderWidth() * 2;
    float bottomBorderWidth = bottomStyle.equals(CSSValue.NONE) ? 0 : borders.bottomBorder.borderWidth() * 2;
    float leftBorderWidth = leftStyle.equals(CSSValue.NONE) ? 0 : borders.leftBorder.borderWidth() * 2;
    float rightBorderWidth = rightStyle.equals(CSSValue.NONE) ? 0 : borders.rightBorder.borderWidth() * 2;

    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(
      cellFragment.posX(Measurement.BORDER) - leftBorderWidth / 2,
      cellFragment.posY(Measurement.BORDER) - topBorderWidth / 2));
    
    float fragmentWidth = TableCellUtil.outerCellWidth(table, cell) + leftBorderWidth / 2 + rightBorderWidth / 2;
    float fragmentHeight = TableCellUtil.outerCellHeight(table, cell) + topBorderWidth / 2 + bottomBorderWidth / 2;

    switch (border.sourceSide()) {
      case TOP -> paintTopBorder(canvas, border, fragmentWidth);
      case BOTTOM -> paintBottomBorder(canvas, border, fragmentWidth, fragmentHeight);
      case LEFT -> paintLeftBorder(canvas, border, fragmentHeight);
      case RIGHT -> paintRightBorder(canvas, border, fragmentWidth, fragmentHeight);
    }

    canvas.popPaint();
  }

  private void assignInitialBorders(
    TableCell cell,
    LayoutConstraint referenceConstraint,
    Set<ComputedBorder> allBorders
  ) {
    TableComputedBorders borders = computeInitialBorders(cell, referenceConstraint);
    TableComputedBorders preservedBorders = cell.preservedBorders();
    preservedBorders.topBorder = borders.topBorder;
    preservedBorders.bottomBorder = borders.bottomBorder;
    preservedBorders.leftBorder = borders.leftBorder;
    preservedBorders.rightBorder = borders.rightBorder;
    allBorders.add(borders.topBorder);
    allBorders.add(borders.bottomBorder);
    allBorders.add(borders.leftBorder);
    allBorders.add(borders.rightBorder);
  }

  private TableComputedBorders computeInitialBorders(TableCell cell, LayoutConstraint referenceConstraint) {
    TableComputedBorders borders = cell.borders();
    borders.topBorder = TableComputedBorders.computeBorder(cell, BorderSide.TOP, referenceConstraint, true);
    borders.bottomBorder = TableComputedBorders.computeBorder(cell, BorderSide.BOTTOM, referenceConstraint, true);
    borders.leftBorder = TableComputedBorders.computeBorder(cell, BorderSide.LEFT, referenceConstraint, true);
    borders.rightBorder = TableComputedBorders.computeBorder(cell, BorderSide.RIGHT, referenceConstraint, true);
    return borders;
  }

  private void assignFinalDimensions(TableCell cell) {
    TableComputedBorders borders = cell.borders();
    cell.cellBox().dimensions().setComputedBorder(
      borders.topBorder.borderWidth(),
      borders.bottomBorder.borderWidth(),
      borders.leftBorder.borderWidth(),
      borders.rightBorder.borderWidth()
    );
  }

  // Specific-Most, then Top-Left-most, come last
  private int compareBorderOrder(ComputedBorder a, ComputedBorder b) {
    if (TableBorderConflictResolver.isCurrentMoreSpecific(b, a)) {
      return 1;
    } else if (TableBorderConflictResolver.isCurrentMoreSpecific(a, b)) {
      return -1;
    }

    int order = TableBorderConflictResolver.compareCellOrder(b.sourceCell(), a.sourceCell());
    if (order != 0) return order;
    
    if (a.sourceSide().ordinal() < b.sourceSide().ordinal()) {
      return 1;
    } else if (a.sourceSide().ordinal() > b.sourceSide().ordinal()) {
      return -1;
    } else return 0;
  }

  private void paintTopBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentWidth
  ) {
    float borderWidth = border.borderWidth() * 2;
    canvas.alterPaint(paint -> paint.setColor(border.borderColor()));
    if (borderWidth > 0) ElementBorderPainter.paintHorizontalBorder(
      canvas,
      0, 0,
      fragmentWidth, borderWidth,
      (BorderStyleValue) border.borderStyle());
  }

  private void paintBottomBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentWidth,
    float fragmentHeight
  ) {
    float borderWidth = border.borderWidth() * 2;
    float bottomBorderY = fragmentHeight - borderWidth;
    canvas.alterPaint(paint -> paint.setColor(border.borderColor()));
    if (borderWidth > 0) ElementBorderPainter.paintHorizontalBorder(
      canvas,
      0, bottomBorderY,
      fragmentWidth, borderWidth,
      (BorderStyleValue) border.borderStyle());
  }

  private void paintLeftBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentHeight
  ) {
    float borderWidth = border.borderWidth() * 2;
    canvas.alterPaint(paint -> paint.setColor(border.borderColor()));
    if (borderWidth > 0) ElementBorderPainter.paintVerticalBorder(
      canvas,
      0, 0,
      fragmentHeight, borderWidth,
      (BorderStyleValue) border.borderStyle());
  }

  private void paintRightBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentWidth,
    float fragmentHeight
  ) {
    float borderWidth = border.borderWidth() * 2;
    float rightBorderX = fragmentWidth - borderWidth;
    canvas.alterPaint(paint -> paint.setColor(border.borderColor()));
    if (borderWidth > 0) ElementBorderPainter.paintVerticalBorder(
      canvas,
      rightBorderX, 0,
      fragmentHeight, borderWidth,
      (BorderStyleValue) border.borderStyle());
  }
  
}
