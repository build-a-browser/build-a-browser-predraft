package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridArea;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridImp;

public interface Grid {

  ElementBox gridBox();

  GridSpan explicitSpan();

  GridSpan implicitSpan();

  void resizeExplicit(GridSpan span);

  void resizeImplicit(GridSpan span);
  
  GridTrack column(int colNum);

  GridTrack row(int rowNum);

  GridTrack track(int trackNum, GridDirection direction);

  GridTrack[] tracks(GridDirection direction);

  GridLine columnLine(int colNum);

  GridLine rowLine(int rowNum);

  GridLine line(int lineNum, GridDirection direction);

  void addArea(GridArea area);

  void placeItem(
    GridItem item,
    int colLineStart,
    int colLineEnd,
    int rowLineStart,
    int rowLineEnd
  );

  GridItem cell(int x, int y, int z);

  boolean isOccupied(int x, int y);

  static Grid create(ElementBox gridBox) {
    return new GridImp(gridBox);
  }

}
