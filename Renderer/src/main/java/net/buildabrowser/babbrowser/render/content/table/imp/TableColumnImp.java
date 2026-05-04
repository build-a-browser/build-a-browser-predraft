package net.buildabrowser.babbrowser.render.content.table.imp;

import net.buildabrowser.babbrowser.render.content.table.Table;
import net.buildabrowser.babbrowser.render.content.table.TableCell;
import net.buildabrowser.babbrowser.render.content.table.TableColumn;

public class TableColumnImp implements TableColumn {

  private final Table table;
  private final int colX;

  // TODO: Need more granular caching
  private float minWidth = Float.NaN;
  private float maxWidth = Float.NaN;

  public TableColumnImp(Table table, int colX) {
    this.table = table;
    this.colX = colX;
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

  private float minContentWidth(int colSpan) {
    if (colSpan > 1) {
      return minContentWidthSpan(colSpan);
    } else {
      return minContentWidthSingle();
    }
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

  public float minContentWidthSpan(int colSpan) {
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

    return Math.max(colSpan, largestMin);
  }

  private float maxContentWidth(int colSpan) {
    if (colSpan > 1) {
      return maxContentWidthSpan(colSpan);
    } else {
      return maxContentWidthSingle();
    }
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

  public float maxContentWidthSpan(int colSpan) {
    float largestMax = minContentWidth(colSpan - 1);
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
    
    return Math.max(colSpan, largestMax);
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

  @Override
  public float usedWidth() {
    // TODO: Properly calculate used width
    return maxContentWidth();
  }
  
}
