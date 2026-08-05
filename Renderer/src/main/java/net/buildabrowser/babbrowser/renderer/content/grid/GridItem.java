package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridItemImp;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public interface GridItem {

  Integer colStart();

  Integer colEnd();

  Integer rowStart();

  Integer rowEnd();

  void setSpan(
    Integer colStart,
    Integer colEnd,
    Integer rowStart,
    Integer rowEnd
  );

  ElementBox itemBox();

  void setRelatedFragment(UnmanagedBoxFragment<?> fragment);

  default GridSpan _gridSpan() {
    Integer colStart = colStart();
    assert colStart != null;
    Integer colEnd = colEnd();
    assert colEnd != null;
    Integer rowStart = rowStart();
    assert rowStart != null;
    Integer rowEnd = rowEnd();
    assert rowEnd != null;

    return new GridSpan(
      colStart, colEnd, rowStart, rowEnd);
  }

  static GridItem create(ElementBox itemBox) {
    return new GridItemImp(itemBox);
  }
  
}
