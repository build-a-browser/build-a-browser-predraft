package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableColumn;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class TableColumnImp implements TableColumn {

  private final Table table;
  private final int colX;
  private final ElementBox columnBox;

  // TODO: Need more granular caching
  private float usedWidth = Float.NaN;
  private float minWidth = Float.NaN;
  private float maxWidth = Float.NaN;

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
  public float usedWidth() {
    return this.usedWidth;
  }

  @Override
  public void setUsedWidth(float usedWidth) {
    this.usedWidth = usedWidth;
  }

  @Override
  public ElementBox columnBox() {
    return this.columnBox;
  }

  @Override
  public boolean isConstrained() {
    // TODO: Account for min/max-content keyword, and group
    ActiveStyles columnStyles = columnBox.activeStyles();
    LayoutConstraint lengthConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      LayoutConstraint.AUTO,
      columnStyles.getProperty(CSSProperty.WIDTH),
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
            cell.cellBox().activeStyles().getProperty(CSSProperty.WIDTH),
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
    ActiveStyles columnStyles = columnBox.activeStyles();
    float minWidth = minContentWidth();
    // TODO: This doesn't support things like the max-content keyword
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      assignableWidth,
      columnStyles.getProperty(CSSProperty.WIDTH),
      false, true);
    if (percentConstraint.isBounded()) {
      return Math.max(minWidth, percentConstraint.value());
    }
    return minWidth;
  }

  @Override
  public float minContentSpecifiedSizingGuess(LayoutConstraint assignableWidth) {
    ActiveStyles columnStyles = columnBox.activeStyles();
    float minWidth = minContentWidth();
    // TODO: This doesn't support things like the max-content keyword
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      assignableWidth,
      columnStyles.getProperty(CSSProperty.WIDTH),
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
    ActiveStyles columnStyles = columnBox.activeStyles();
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      assignableWidth,
      columnStyles.getProperty(CSSProperty.WIDTH),
      false, true);
    if (percentConstraint.isBounded()) {
      float minWidth = minContentWidth();
      return Math.max(minWidth, percentConstraint.value());
    }
    return maxContentWidth();
  }

  @Override
  public float minContentWidth() {
    if (Float.isNaN(minWidth)) {
      this.minWidth = minContentWidth(largestColSpan());
    }
    return this.minWidth;
  }

  @Override
  public float maxContentWidth() {
    if (Float.isNaN(maxWidth)) {
      this.maxWidth = maxContentWidth(largestColSpan());
    }
    return this.maxWidth;
  }

  @Override
  public float minContentWidth(int colSpan) {
    if (colSpan > 1) {
      return minContentWidthSpan(colSpan);
    } else {
      return minContentWidthSingle();
    }
  }

  @Override
  public float maxContentWidth(int colSpan) {
    if (colSpan > 1) {
      return maxContentWidthSpan(colSpan);
    } else {
      return maxContentWidthSingle();
    }
  }

  @Override
  public float intrinsicPercentage() {
    ActiveStyles columnStyles = columnBox.activeStyles();
    LayoutConstraint percentConstraint = SizingUtil.evaluateBaseSize(
      columnBox.layoutContext(),
      LayoutConstraint.of(100),
      columnStyles.getProperty(CSSProperty.WIDTH),
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
