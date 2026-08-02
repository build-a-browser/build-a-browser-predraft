package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import net.buildabrowser.babbrowser.renderer.content.grid.Grid;
import net.buildabrowser.babbrowser.renderer.content.grid.GridItem;
import net.buildabrowser.babbrowser.renderer.content.grid.GridSpan;

public class GridImp implements Grid {

  private GridSpan explicitSpan;

  @Override
  public GridSpan explicitSpan() {
    return this.explicitSpan;
  }

  @Override
  public GridSpan implicitSpan() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'implicitSpan'");
  }

  @Override
  public void resizeExplicit(GridSpan span) {
    this.explicitSpan = span;
  }

  @Override
  public void resizeImplicit(GridSpan span) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'resizeImplicit'");
  }

  @Override
  public void placeRowLine(String lineName, int rowNum) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'placeRowLine'");
  }

  @Override
  public void placeColumnLine(String lineName, int colNum) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'placeColumnLine'");
  }

  @Override
  public int linePos(String name, int index) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'linePos'");
  }

  @Override
  public void placeItem(GridItem item, int rowStart, int rowEnd, int colStart, int colEnd) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'placeItem'");
  }
  
}
