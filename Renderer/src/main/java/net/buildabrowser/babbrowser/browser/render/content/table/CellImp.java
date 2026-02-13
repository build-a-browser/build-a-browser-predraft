package net.buildabrowser.babbrowser.browser.render.content.table;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.table.Table.Cell;

// Unfortunately cannot be a record since height and relatedFragment are mutable...
public class CellImp implements Cell {

  private final ElementBox cellBox;
  private final int cellX, cellY;
  private final int width;
  
  private int height;
  private UnmanagedBoxFragment relatedFragment;

  public CellImp(int cellX, int cellY, int width, int height, ElementBox cellBox) {
    this.cellBox = cellBox;
    this.cellX = cellX;
    this.cellY = cellY;
    this.width = width;
    this.height = height;
  };

  @Override
  public int cellX() {
    return this.cellX;
  }

  @Override
  public int cellY() {
    return this.cellY;
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public ElementBox cellBox() {
    return this.cellBox;
  }

  public void extend(int heightExtension) {
    this.height += heightExtension;
  }

  @Override
  public void setRelatedFragment(UnmanagedBoxFragment fragment) {
    this.relatedFragment = fragment;
  }

  @Override
  public UnmanagedBoxFragment getRelatedFragment() {
    return this.relatedFragment;
  }

}