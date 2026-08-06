package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridImp;

public interface Grid {

  GridSpan explicitSpan();

  GridSpan implicitSpan();

  void resizeExplicit(GridSpan span);

  void resizeImplicit(GridSpan span);
  
  GridTrack column(int colNum);

  GridTrack row(int rowNum);

  GridLine columnLine(int colNum);

  GridLine rowLine(int rowNum);
  
  void addArea(GridArea area);

  void placeItem(
    GridItem item,
    int colLineStart,
    int colLineEnd,
    int rowLineStart,
    int rowLineEnd
  );

  GridItem cell(int x, int y, int z);

  static Grid create() {
    return new GridImp();
  }

}
