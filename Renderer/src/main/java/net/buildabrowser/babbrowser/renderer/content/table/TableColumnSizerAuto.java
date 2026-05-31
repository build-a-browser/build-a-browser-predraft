package net.buildabrowser.babbrowser.renderer.content.table;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.content.table.TableContent.BorderSpacings;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class TableColumnSizerAuto {

  private static final ColumnSizer[] TABLE_SIZERS = new ColumnSizer[] {
    TableColumn::minContentSizingGuess,
    TableColumn::minContentPercentageSizingGuess,
    TableColumn::minContentSpecifiedSizingGuess,
    TableColumn::maxContentSizingGuess
  };
  
  private TableColumnSizerAuto() {}

  public static void assignTableWidths(
    LayoutConstraint widthConstraint,
    List<TableColumn> columns,
    BorderSpacings borderSpacings
  ) {
    LayoutConstraint assignableWidth = widthConstraint.isBounded() ?
      LayoutConstraint.of(Math.max(0,
        widthConstraint.value() - borderSpacings.hSpace() * (columns.size() + 1))) :
      widthConstraint;
    if (widthConstraint.isBounded()) {
      boolean didAssign = assignInterpolatedTableWidths(assignableWidth, columns);
      if (didAssign) return;
    }

    for (TableColumn column: columns) {
      column.setUsedWidth(column.maxContentSizingGuess(assignableWidth));
    }
    if (!widthConstraint.isBounded()) return;
    distributeExcessWidthAuto(assignableWidth, columns);
  }

  private static boolean assignInterpolatedTableWidths(
    LayoutConstraint assignableWidth,
    List<TableColumn> columns
  ) {
    float closestUnder = -1;
    float[] closestUnderValues = null;

    float closestOver = Float.MAX_VALUE;
    float[] closestOverValues = null;

    for (ColumnSizer sizer: TABLE_SIZERS) {
      float[] columnSizes = determineColumnSizes(columns, assignableWidth, sizer);
      float constraintTotal = TableSizeUtil.sumSizes(columnSizes);
      if (
        constraintTotal > closestUnder
        && constraintTotal <= assignableWidth.floatValue()
      ) {
        closestUnder = constraintTotal;
        closestUnderValues = columnSizes;
      } else if (
        constraintTotal < closestOver
        && constraintTotal >= assignableWidth.floatValue()
      ) {
        closestOver = constraintTotal;
        closestOverValues = columnSizes;
      }
    }

    if (closestOverValues == null) return false;

    float overWeight =
      closestOver == closestUnder ? 0 :
      (assignableWidth.value() - closestUnder) / (closestOver - closestUnder);

    int i = 0;
    for (TableColumn column: columns) {
      float columnUnder = closestUnderValues[i];
      float columnOver = closestOverValues[i++];
      column.setUsedWidth(columnUnder + (columnOver - columnUnder) * overWeight);
    }

    return true;
  }

  private static float[] determineColumnSizes(
    List<TableColumn> columns,
    LayoutConstraint assignableWidth,
    ColumnSizer sizer
  ) {
    float[] columnSizes = new float[columns.size()];
    int i = 0;
    for (TableColumn column: columns) {
      columnSizes[i++] += sizer.size(column, assignableWidth);
    }

    return columnSizes;
  }
  
  private static void distributeExcessWidthAuto(
    LayoutConstraint assignableWidth,
    List<TableColumn> columns
  ) {
    float excessWidth = assignableWidth.value();
    for (TableColumn column: columns) {
      excessWidth -= column.maxContentSizingGuess(assignableWidth);
    }
    if (excessWidth <= 0) return;
    
    if (hasColumn(columns, TableColumnSizerAuto::isRule1Column)) {
      distributeExcessWidthAutoRule1(excessWidth, columns);
    } else if (hasColumn(columns, TableColumnSizerAuto::isRule2Column)) {
      distributeExcessWidthAutoRule2(excessWidth, columns);
    } else if (hasColumn(columns, TableColumnSizerAuto::isRule3Column)) {
      distributeExcessWidthAutoRule3(excessWidth, columns);
    } else if (hasColumn(columns, TableColumnSizerAuto::isRule4Column)) {
      distributeExcessWidthAutoRule4(excessWidth, columns);
    } else if (hasColumn(columns, TableColumnSizerAuto::isRule5Column)) {
      distributeExcessWidthAutoRule5(excessWidth, columns);
    } else {
      distributeExcessWidthAutoRule6(excessWidth, columns);
    }
  }

  private static void distributeExcessWidthAutoRule1(
    float excessWidth, List<TableColumn> columns
  ) {
    float totalMaxContentWidth = 0;
    for (TableColumn column: columns) {
      if (isRule1Column(column)) {
        totalMaxContentWidth += column.maxContentWidth();
      }
    }

    for (TableColumn column: columns) {
      if (isRule1Column(column)) {
        float widthChunk = column.maxContentWidth() / totalMaxContentWidth * excessWidth;
        column.setUsedWidth(column.usedWidth() + widthChunk);
      }
    }
  }

  private static void distributeExcessWidthAutoRule2(
    float excessWidth, List<TableColumn> columns
  ) {
    int numRuleColumns = 0;
    for (TableColumn column: columns) {
      if (isRule2Column(column)) {
        numRuleColumns++;
      }
    }

    for (TableColumn column: columns) {
      if (isRule2Column(column)) {
        float widthChunk = excessWidth / numRuleColumns;
        column.setUsedWidth(column.usedWidth() + widthChunk);
      }
    }
  }

  private static void distributeExcessWidthAutoRule3(
    float excessWidth, List<TableColumn> columns
  ) {
    float totalMaxContentWidth = 0;
    for (TableColumn column: columns) {
      if (isRule3Column(column)) {
        totalMaxContentWidth += column.maxContentWidth();
      }
    }

    for (TableColumn column: columns) {
      if (isRule3Column(column)) {
        float widthChunk = column.maxContentWidth() / totalMaxContentWidth * excessWidth;
        column.setUsedWidth(column.usedWidth() + widthChunk);
      }
    }
  }

  private static void distributeExcessWidthAutoRule4(
    float excessWidth, List<TableColumn> columns
  ) {
    float totalIntrinsicPercents = 0;
    for (TableColumn column: columns) {
      if (isRule4Column(column)) {
        totalIntrinsicPercents += column.intrinsicPercentage();
      }
    }

    for (TableColumn column: columns) {
      if (isRule4Column(column)) {
        float widthChunk = column.intrinsicPercentage() / totalIntrinsicPercents * excessWidth;
        column.setUsedWidth(column.usedWidth() + widthChunk);
      }
    }
  }

  private static void distributeExcessWidthAutoRule5(
    float excessWidth, List<TableColumn> columns
  ) {
    int numRuleColumns = 0;
    for (TableColumn column: columns) {
      if (isRule5Column(column)) {
        numRuleColumns++;
      }
    }

    for (TableColumn column: columns) {
      if (isRule5Column(column)) {
        float widthChunk = excessWidth / numRuleColumns;
        column.setUsedWidth(column.usedWidth() + widthChunk);
      }
    }
  }

  private static void distributeExcessWidthAutoRule6(
    float excessWidth, List<TableColumn> columns
  ) {
    float widthPer = excessWidth / columns.size();
    for (TableColumn column: columns) {
      column.setUsedWidth(column.usedWidth() + widthPer);
    }
  }

  private static boolean isRule1Column(TableColumn column) {
    return
      !column.isConstrained()
      && column.intrinsicPercentage() == 0
      && column.maxContentWidth() != 0;
  }

  private static boolean isRule2Column(TableColumn column) {
    return
      !column.isConstrained()
      && column.intrinsicPercentage() == 0;
  }

  private static boolean isRule3Column(TableColumn column) {
    return
      column.isConstrained()
      && column.intrinsicPercentage() == 0
      && column.maxContentWidth() != 0;
  }

  private static boolean isRule4Column(TableColumn column) {
    return column.intrinsicPercentage() > 0;
  }

  private static boolean isRule5Column(TableColumn column) {
    return column.hasOriginatingCells();
  }

  private static boolean hasColumn(List<TableColumn> columns, ColumnChecker checker) {
    for (TableColumn column: columns) {
      if (checker.check(column)) return true;
    }

    return false;
  }

  public static interface ColumnSizer {
  
    float size(TableColumn column, LayoutConstraint assignableWidth);

  }

  public static interface ColumnChecker {
  
    boolean check(TableColumn column);

  }

}
