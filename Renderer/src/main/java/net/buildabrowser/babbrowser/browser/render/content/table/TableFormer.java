package net.buildabrowser.babbrowser.browser.render.content.table;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.table.Table.Cell;
import net.buildabrowser.babbrowser.browser.render.content.table.Table.RowGroup;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;

public final class TableFormer {
  
  private TableFormer() {}

  public static Table formTable(Table table, ElementBox refBox) {
    ListIterator<Box> childIt = refBox.childBoxes();
    if (!childIt.hasNext()) {
      table.markSize(0, 0);
      return table;
    }

    TableFormerBookkeeping bookkeeping = new TableFormerBookkeeping();
    
    Box currentElement = advanceOrFinish(childIt, table, bookkeeping);
    while (!firstElementCheck(currentElement)) {
      currentElement = advanceOrFinish(childIt, table, bookkeeping);
      if (currentElement == null) {
        table.markSize(0, 0);
        return table;
      }
    }

    while (TableBoxUtil.isColumnGroup(currentElement)) {
      processColumnGroup(table, currentElement);
      currentElement = advanceOrFinish(childIt, table, bookkeeping);
    }
    
    while (currentElement != null) {
      if (currentElement instanceof ElementBox elementBox) {
        switch (elementBox.activeStyles().outerDisplayValue()) {
          case TABLE_ROW -> processRow(table, bookkeeping, elementBox);
          case TABLE_FOOTER_GROUP -> {
            endRowGroup(table, bookkeeping);
            bookkeeping.pendingTfootElements.add(elementBox);
          }
          case TABLE_HEADER_GROUP, TABLE_ROW_GROUP -> {
            endRowGroup(table, bookkeeping);
            processRowGroup(table, bookkeeping, elementBox);
          }
          default -> {}
        }
      }
      currentElement = advanceOrFinish(childIt, table, bookkeeping);
    }

    table.markSize(bookkeeping.xWidth, bookkeeping.yHeight);
    return table;
  }

  private static void processRowGroup(
    Table table, TableFormerBookkeeping bookkeeping, ElementBox refBox
  ) {
    int yStart = bookkeeping.yHeight;
    for (Box childBox: refBox.childBoxes()) {
      if (
        childBox instanceof ElementBox elementBox
        && TableBoxUtil.isTableRow(elementBox)
      ) {
        processRow(table, bookkeeping, elementBox);
      }
    }
    if (bookkeeping.yHeight > yStart) {
      table.assignRowGroup(new RowGroup(yStart, bookkeeping.yHeight - yStart, refBox));
    }
    endRowGroup(table, bookkeeping);
  }

  private static void growDownwardGrowingCells(Table table, TableFormerBookkeeping bookkeeping) {
    for (Cell cell: bookkeeping.downwardGrowingCells) {
      table.extendCellY(cell, bookkeeping.yCurrent);
    }
  }

  private static void processRow(
    Table table, TableFormerBookkeeping bookkeeping, ElementBox refBox
  ) {
    if (bookkeeping.yHeight > bookkeeping.yCurrent) {
      bookkeeping.yCurrent++;
    }
    int xCurrent = 0;
    growDownwardGrowingCells(table, bookkeeping);

    ListIterator<Box> childIt = refBox.childBoxes();

    while (childIt.hasNext()) {
      ElementBox currentCell = (ElementBox) childIt.next();
      // TODO: Verify that this is a td or th (should be from fixup)
      while (table.isSlotAssigned(xCurrent, bookkeeping.yCurrent)) {
        xCurrent++;
      }
      if (xCurrent == bookkeeping.xWidth) {
        bookkeeping.xWidth++;
      }
      // TODO: Parse span attributes
      int colspan = 1, rowspan = 1;
      boolean cellGrowsDownward = rowspan == 0;
      rowspan = cellGrowsDownward ? 1 : rowspan;
      bookkeeping.xWidth = Math.max(bookkeeping.xWidth, xCurrent + colspan);
      bookkeeping.yHeight = Math.max(bookkeeping.yHeight, bookkeeping.yCurrent + rowspan);
      Cell c = table.createCell(xCurrent, bookkeeping.yCurrent, rowspan, colspan, currentCell);
      // TODO: Headers and stuff, also record any table model error
      if (cellGrowsDownward) {
        bookkeeping.downwardGrowingCells.add(c);
      }
      xCurrent += colspan;
    }
    bookkeeping.yCurrent++;
  }

  private static void endRowGroup(Table table, TableFormerBookkeeping bookkeeping) {
    while (bookkeeping.yCurrent < bookkeeping.yHeight) {
      growDownwardGrowingCells(table, bookkeeping);
      bookkeeping.yCurrent++;
    }
    bookkeeping.downwardGrowingCells.clear(); 
  }

  private static void processColumnGroup(Table table, Box currentElement) {
    // TODO: Handle column groups
  }

  private static Box advanceOrFinish(
    ListIterator<Box> childIt, Table table, TableFormerBookkeeping bookkeeping
  ) {
    if (!childIt.hasNext()) {
      finish(table, bookkeeping);
      return null;
    }

    Box nextBox = childIt.next();
    // TODO: This would be a good spot to associate the caption
    return nextBox;
  }

  private static void finish(Table table, TableFormerBookkeeping bookkeeping) {
    for (ElementBox tfootElement: bookkeeping.pendingTfootElements) {
      processRowGroup(table, bookkeeping, tfootElement);
    }
    // TODO: Handle any table model error
  }

  private static boolean firstElementCheck(Box currentElement) {
    if (!(currentElement instanceof ElementBox elBox)) return false;
    OuterDisplayValue displayValue = elBox.activeStyles().outerDisplayValue();
    return switch (displayValue) {
      case TABLE_COLUMN_GROUP -> true;
      case TABLE_HEADER_GROUP -> true;
      case TABLE_ROW_GROUP -> true;
      case TABLE_FOOTER_GROUP -> true;
      case TABLE_ROW -> true;
      default -> false;
    };
  }

  private static class TableFormerBookkeeping {
    private final List<ElementBox> pendingTfootElements = new ArrayList<>();
    private final List<Cell> downwardGrowingCells = new ArrayList<>();
    private int xWidth = 0;
    private int yHeight = 0;
    private int yCurrent = 0;
  }

}
