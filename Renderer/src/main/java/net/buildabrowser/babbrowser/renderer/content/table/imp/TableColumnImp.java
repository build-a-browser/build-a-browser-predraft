package net.buildabrowser.babbrowser.renderer.content.table.imp;

import java.util.Arrays;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.table.Table;
import net.buildabrowser.babbrowser.renderer.content.table.TableCell;
import net.buildabrowser.babbrowser.renderer.content.table.TableColumn;
import net.buildabrowser.babbrowser.renderer.content.table.TableComputedBorders;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class TableColumnImp implements TableColumn {

  private final TableComputedBorders borders = new TableComputedBorders();

  private final Table table;
  private final int colX;
  private final ElementBox columnBox;

  private float usedWidth = Float.NaN;
  // TODO: This is probably terrible for memory
  private float[] minWidths;
  private float[] maxWidths;

  public TableColumnImp(
    Table table,
    int colX,
    ElementBox columnBox
  ) {
    this.table = table;
    this.colX = colX;
    this.columnBox = columnBox;
  }

  @Override
  public ElementBox columnBox() {
    return this.columnBox;
  }

  @Override
  public TableComputedBorders borders() {
    return this.borders;
  }

  @Override
  public float usedWidth() {
    return this.usedWidth;
  }

  @Override
  public void setUsedWidth(float usedWidth) {
    this.usedWidth = usedWidth;
  }

  @Override
  public boolean isConstrained() {
    // TODO: Account for min/max-content keyword, and group
    LayoutConstraint lengthConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      LayoutConstraint.AUTO,
      columnBox.properties().get(CSSProperty.WIDTH),
      true, false);
    if (lengthConstraint.isBounded()) return true;

    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        if (cell.width() == 1) {
          LayoutConstraint childLengthConstraint = SizingUtil.evaluateBaseSize(
            cell.cellBox().layoutContext(),
            LayoutConstraint.AUTO,
            cell.cellBox().properties().get(CSSProperty.WIDTH),
            true, false);
          if (childLengthConstraint.isBounded()) return true;
        }
        cell = table.cell(colX, y, ++i);
      }
    }

    return false;
  }

  @Override
  public float minContentSizingGuess(LayoutConstraint assignableWidth) {
    return minContentWidth();
  }

  @Override
  public float minContentPercentageSizingGuess(LayoutConstraint assignableWidth) {
    float minWidth = minContentWidth();
    // TODO: This doesn't support things like the max-content keyword
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      assignableWidth,
      columnBox.properties().get(CSSProperty.WIDTH),
      false, true);
    if (percentConstraint.isBounded()) {
      return Math.max(minWidth, percentConstraint.value());
    }
    return minWidth;
  }

  @Override
  public float minContentSpecifiedSizingGuess(LayoutConstraint assignableWidth) {
    float minWidth = minContentWidth();
    // TODO: This doesn't support things like the max-content keyword
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      assignableWidth,
      columnBox.properties().get(CSSProperty.WIDTH),
      false, true);
    if (percentConstraint.isBounded()) {
      return Math.max(minWidth, percentConstraint.value());
    }

    if (isConstrained()) {
      return maxContentWidth();
    }

    return minWidth;
  }

  @Override
  public float maxContentSizingGuess(LayoutConstraint assignableWidth) {
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      assignableWidth,
      columnBox.properties().get(CSSProperty.WIDTH),
      false, true);
    if (percentConstraint.isBounded()) {
      float minWidth = minContentWidth();
      return Math.max(minWidth, percentConstraint.value());
    }
    return maxContentWidth();
  }

  @Override
  public float minContentWidth() {
    return minContentWidth(largestColSpan());
  }

  @Override
  public float maxContentWidth() {
    return maxContentWidth(largestColSpan());
  }

  @Override
  public float minContentWidth(int colSpan) {
    if (this.minWidths == null) {
      this.minWidths = new float[largestColSpan()];
      Arrays.fill(this.minWidths, Float.NaN);
    }

    if (Float.isNaN(this.minWidths[colSpan - 1])) {
      return this.minWidths[colSpan - 1] = colSpan > 1 ?
        minContentWidthSpan(colSpan) :
        minContentWidthSingle();
    }

    return this.minWidths[colSpan - 1];
  }

  @Override
  public float maxContentWidth(int colSpan) {
    if (this.maxWidths == null) {
      this.maxWidths = new float[largestColSpan()];
      Arrays.fill(this.maxWidths, Float.NaN);
    }

    if (Float.isNaN(this.maxWidths[colSpan - 1])) {
      return this.maxWidths[colSpan - 1] = colSpan > 1 ?
        maxContentWidthSpan(colSpan) :
        maxContentWidthSingle();
    }
    
    return this.maxWidths[colSpan - 1];
  }

  @Override
  public float intrinsicPercentage() {
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      LayoutConstraint.of(100),
      columnBox.properties().get(CSSProperty.WIDTH),
      false, true);

    if (!percentConstraint.isBounded()) return -1;
    if (percentConstraint.value() < 0) return -1;
    return percentConstraint.value();
  }

  @Override
  public boolean hasOriginatingCells() {
    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        if (cell.cellX() == colX) return true;
        cell = table.cell(colX, y, ++i);
      }
    }

    return false;
  }

  private float minContentWidthSingle() {
    // TODO: Query the column details itself
    // TODO: Handle fixed mode
    float largestMin = 0;
    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        if (cell.width() == 1) {
          float cellSpan = cell.outerMinContentWidth();
          largestMin = Math.max(largestMin, cellSpan);
        }
        cell = table.cell(colX, y, ++i);
      }
    }

    return largestMin;
  }

  private float minContentWidthSpan(int colSpan) {
    float largestMin = minContentWidth(colSpan - 1);
    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        if (cell.width() == colSpan) {
          float cellSpan = cell.minContentContribution(colX);
          largestMin = Math.max(largestMin, cellSpan);
        }
        cell = table.cell(colX, y, ++i);
      }
    }

    return largestMin;
  }

  private float maxContentWidthSingle() {
    // TODO: Query the column details itself
    // TODO: Handle fixed mode
    float largestMax = 0;
    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        if (cell.width() == 1) {
          float cellSpan = cell.outerMaxContentWidth();
          largestMax = Math.max(largestMax, cellSpan);
        }
        cell = table.cell(colX, y, ++i);
      }
    }

    return largestMax;
  }

  private float maxContentWidthSpan(int colSpan) {
    float largestMax = maxContentWidth(colSpan - 1);
    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        if (cell.width() == colSpan) {
          float cellSpan = cell.maxContentContribution(colX);
          largestMax = Math.max(largestMax, cellSpan);
        }
        cell = table.cell(colX, y, ++i);
      }
    }
    
    return largestMax;
  }

  private int largestColSpan() {
    int largestColSpan = 0;
    for (int y = 0; y < table.height(); y++) {
      int i = 0;
      TableCell cell = table.cell(colX, y, i);
      while (cell != null) {
        largestColSpan = Math.max(largestColSpan, cell.width());
        cell = table.cell(colX, y, ++i);
      }
    }

    return largestColSpan;
  }
  
}
