package net.buildabrowser.babbrowser.render.content.table;

import java.util.List;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.table.TableContent.BorderSpacings;
import net.buildabrowser.babbrowser.render.content.table.imp.TableImp;

public interface Table {
  
  boolean isSlotAssigned(int x, int y);

  void assignRowGroup(RowGroup group);

  TableCell createCell(int cellX, int cellY, int initWidth, int initHeight, ElementBox cellBox);

  TableCell cell(int cellX, int cellY, int layer);

  void extendCellY(TableCell cell, int targetY);

  void markSize(int width, int height);

  void createTracks();

  List<TableColumn> columns();

  List<TableRow> rows();

  List<ColumnGroup> columnGroups();

  List<RowGroup> rowGroups();

  TableColumn column(int colX);

  TableRow row(int rowY);

  BorderSpacings spacings();

  int width();

  int height();

  static record RowGroup(int yStart, int yHeight, ElementBox groupBox) {}

  static record ColumnGroup(int xStart, int xWidth, ElementBox groupBox) {}

  static Table create(
    ElementBox tableBox, BorderSpacings spacings
  ) {
    return new TableImp(tableBox, spacings);
  }

}
