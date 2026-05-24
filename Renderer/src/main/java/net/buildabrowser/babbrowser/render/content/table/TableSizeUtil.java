package net.buildabrowser.babbrowser.render.content.table;

import java.util.List;

public final class TableSizeUtil {
  
  private TableSizeUtil() {}

  public static float sumMinWidths(List<TableColumn> columns) {
    float totalWidth = 0;
    for (TableColumn column: columns) {
      totalWidth += column.minContentWidth();
    }

    return totalWidth;
  }

  public static float sumMaxWidths(List<TableColumn> columns) {
    float totalWidth = 0;
    for (TableColumn column: columns) {
      totalWidth += column.maxContentWidth();
    }

    return totalWidth;
  }

  public static float sumSizes(float[] columnWidths) {
    float totalWidth = 0;
    for (float width: columnWidths) {
      totalWidth += width;
    }

    return totalWidth;
  }

}
