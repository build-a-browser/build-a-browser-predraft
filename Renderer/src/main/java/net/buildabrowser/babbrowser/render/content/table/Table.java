package net.buildabrowser.babbrowser.render.content.table;

import java.util.List;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.table.imp.TableImp;

public interface Table {
  
  boolean isSlotAssigned(int x, int y);

  void assignRowGroup(RowGroup group);

  TableCell createCell(int cellX, int cellY, int initWidth, int initHeight, ElementBox cellBox);

  TableCell cell(int cellX, int cellY, int layer);

  void extendCellY(TableCell cell, int targetY);

  void markSize(int width, int height);

  void createColumns();

  List<TableColumn> columns();

  TableColumn column(int colX);

  int width();

  int height();

  static record RowGroup(int yStart, int yHeight, ElementBox groupBox) {}

  static Table create() {
    return new TableImp();
  }

}
