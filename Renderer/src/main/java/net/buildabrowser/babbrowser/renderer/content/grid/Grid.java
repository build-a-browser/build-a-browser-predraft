package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridImp;

public interface Grid {

  GridSpan explicitSpan();

  GridSpan implicitSpan();

  void resizeExplicit(GridSpan span);

  void resizeImplicit(GridSpan span);
  
  void placeRowLine(
    String lineName,
    int rowNum
  );

  void placeColumnLine(
    String lineName,
    int colNum
  );

  int linePos(String name, int index);

  void placeItem(
    GridItem item,
    int rowStart,
    int rowEnd,
    int colStart,
    int colEnd
  );

  static Grid create() {
    return new GridImp();
  }

}
