package net.buildabrowser.babbrowser.renderer.paint.painters.table;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders.ComputedBorder;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableBorderAssignment;
import net.buildabrowser.babbrowser.renderer.content.table.imp.border.TableCollapsedBorderAssigner.SlotComputedBorder;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBorderPainter;

public class TableCollapsedBorderPainter implements TableBorderPainter {
  
  private static final ComputedBorder GENERIC_BORDER = new ComputedBorder(
    null, null, 0, CSSValue.NONE);

  @Override
  public void paintSavedBorders(
    PaintCanvas canvas, 
    TableBorderAssignment assignment,
    Table table
  ) {
    TableComputedBorders[][] slotGrid = assignment.slotGrid();
    for (SlotComputedBorder border: assignment.borderOrder()) {
      paintSavedBorder(canvas, table, slotGrid, border);
    }
  }

  private void paintSavedBorder(
    PaintCanvas canvas,
    Table table,
    TableComputedBorders[][] slotGrid,
    SlotComputedBorder slotBorder
  ) {    
    BoxFragment<?> columnFragment = table.column(slotBorder.x()).columnBox().positioningFragment();
    BoxFragment<?> rowFragment = table.row(slotBorder.y()).rowBox().positioningFragment();

    TableComputedBorders borders = slotGrid[slotBorder.y()][slotBorder.x()];
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
    
    float fragmentWidth = table.column(slotBorder.x()).usedWidth() + leftBorderWidth / 2 + rightBorderWidth / 2;
    float fragmentHeight = table.row(slotBorder.y()).usedHeight() + topBorderWidth / 2 + bottomBorderWidth / 2;

    if (border.borderWidth() > 0) canvas.withPaintAndTransform(
      p -> p.setColor(border.borderColor()),
      t -> t.translate(
        columnFragment.posX(Measurement.BORDER) - leftBorderWidth / 2,
        rowFragment.posY(Measurement.BORDER) - topBorderWidth / 2),
      c -> { switch (border.sourceSide()) {
        case TOP -> paintTopBorder(canvas, border, fragmentWidth);
        case BOTTOM -> paintBottomBorder(canvas, border, fragmentWidth, fragmentHeight);
        case LEFT -> paintLeftBorder(canvas, border, fragmentHeight);
        case RIGHT -> paintRightBorder(canvas, border, fragmentWidth, fragmentHeight);
      }});
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
    ElementBorderPainter.paintHorizontalBorder(
      canvas,
      0, 0,
      fragmentWidth, borderWidth,
      (LineStyleValue) border.borderStyle());
  }

  private void paintBottomBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentWidth,
    float fragmentHeight
  ) {
    float borderWidth = border.borderWidth() * 2;
    float bottomBorderY = fragmentHeight - borderWidth;
    ElementBorderPainter.paintHorizontalBorder(
      canvas,
      0, bottomBorderY,
      fragmentWidth, borderWidth,
      (LineStyleValue) border.borderStyle());
  }

  private void paintLeftBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentHeight
  ) {
    float borderWidth = border.borderWidth() * 2;
    if (borderWidth > 0) ElementBorderPainter.paintVerticalBorder(
      canvas,
      0, 0,
      fragmentHeight, borderWidth,
      (LineStyleValue) border.borderStyle());
  }

  private void paintRightBorder(
    PaintCanvas canvas,
    ComputedBorder border,
    float fragmentWidth,
    float fragmentHeight
  ) {
    float borderWidth = border.borderWidth() * 2;
    float rightBorderX = fragmentWidth - borderWidth;
    ElementBorderPainter.paintVerticalBorder(
      canvas,
      rightBorderX, 0,
      fragmentHeight, borderWidth,
      (LineStyleValue) border.borderStyle());
  }
  
}
