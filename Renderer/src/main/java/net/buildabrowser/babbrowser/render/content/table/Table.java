package net.buildabrowser.babbrowser.render.content.table;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;

public interface Table {
  
  boolean isSlotAssigned(int x, int y);

  void assignRowGroup(RowGroup group);

  Cell createCell(int cellX, int cellY, int initWidth, int initHeight, ElementBox cellBox);

  Cell getCell(int cellX, int cellY, int layer);

  void extendCellY(Cell cell, int targetY);

  void markSize(int width, int height);

  int width();

  int height();

  static record RowGroup(int yStart, int yHeight, ElementBox groupBox) {}

  static interface Cell {
    
    int cellX();
    
    int cellY();

    int width();

    int height();

    ElementBox cellBox();

    void setRelatedFragment(UnmanagedBoxFragment fragment);

    UnmanagedBoxFragment getRelatedFragment();

    // TODO: Associated header cells

  }

  static Table create() {
    return new TableImp();
  }

}
