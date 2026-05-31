package net.buildabrowser.babbrowser.render.content.table.imp.collapsed;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.paint.ElementBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableBorderPainter;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.render.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.render.content.table.imp.collapsed.TableCollapsedBorderAssigner.SlotComputedBorder;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class TableCollapsedBorderPainter implements TableBorderPainter {

  private static final ComputedBorder GENERIC_BORDER = new ComputedBorder(null, null, 0, CSSValue.NONE);

  private final TableCollapsedBorderAssigner assigner = new TableCollapsedBorderAssigner();

  @Override
  public void assignBorders(
    Table table,
    LayoutConstraint referenceConstraint
  ) {
    assigner.assignBorders(table);
  }

  @Override
  public void paintSavedBorders(
    PaintCanvas canvas, Table table
  ) {
    for (SlotComputedBorder border: assigner.borderOrder()) {
      paintSavedBorder(canvas, table, border);
    }
  }

  private void paintSavedBorder(PaintCanvas canvas, Table table, SlotComputedBorder slotBorder) {    
    BoxFragment columnFragment = table.column(slotBorder.x()).columnBox().positioningFragment();
    BoxFragment rowFragment = table.row(slotBorder.y()).rowBox().positioningFragment();

    TableComputedBorders borders = assigner.slotGrid()[slotBorder.y()][slotBorder.x()];
    ComputedBorder border = slotBorder.border();

    ComputedBorder topBorder = borderOrGeneric(borders.topBorder);
    ComputedBorder bottomBorder = borderOrGeneric(borders.bottomBorder);
    ComputedBorder leftBorder = borderOrGeneric(borders.leftBorder);
    ComputedBorder rightBorder = borderOrGeneric(borders.rightBorder);

    CSSValue topStyle = topBorder.borderStyle();
    CSSValue bottomStyle = bottomBorder.borderStyle();
    CSSValue leftStyle = leftBorder.borderStyle();
    CSSValue rightStyle = rightBorder.borderStyle();

    // TODO: This should be done at compute?
    float topBorderWidth = topStyle.equals(CSSValue.NONE) ? 0 : topBorder.borderWidth() * 2;
    float bottomBorderWidth = bottomStyle.equals(CSSValue.NONE) ? 0 : bottomBorder.borderWidth() * 2;
    float leftBorderWidth = leftStyle.equals(CSSValue.NONE) ? 0 : leftBorder.borderWidth() * 2;
    float rightBorderWidth = rightStyle.equals(CSSValue.NONE) ? 0 : rightBorder.borderWidth() * 2;

    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(
      columnFragment.posX(Measurement.BORDER) - leftBorderWidth / 2,
      rowFragment.posY(Measurement.BORDER) - topBorderWidth / 2));
    
    float fragmentWidth = table.column(slotBorder.x()).usedWidth() + leftBorderWidth / 2 + rightBorderWidth / 2;
    float fragmentHeight = table.row(slotBorder.y()).usedHeight() + topBorderWidth / 2 + bottomBorderWidth / 2;

    switch (border.sourceSide()) {
      case TOP -> paintTopBorder(canvas, border, fragmentWidth);
      case BOTTOM -> paintBottomBorder(canvas, border, fragmentWidth, fragmentHeight);
      case LEFT -> paintLeftBorder(canvas, border, fragmentHeight);
      case RIGHT -> paintRightBorder(canvas, border, fragmentWidth, fragmentHeight);
    }

    canvas.popPaint();
  }

  private ComputedBorder borderOrGeneric(ComputedBorder border) {
    if (border != null) return border;
    return GENERIC_BORDER;
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
