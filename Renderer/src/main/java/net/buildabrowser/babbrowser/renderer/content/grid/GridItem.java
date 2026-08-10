package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridItemImp;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;

public interface GridItem {

  Integer colLineStart();

  Integer colLineEnd();

  Integer rowLineStart();

  Integer rowLineEnd();

  Integer lineStart(GridDirection direction);

  Integer lineEnd(GridDirection direction);

  void setSpan(
    Integer colStart,
    Integer colEnd,
    Integer rowStart,
    Integer rowEnd
  );

  int fallbackSpan();
  
  void setFallbackSpan(int fallbackSpan);

  ElementBox itemBox();

  void setRelatedFragment(UnmanagedBoxFragment<?> fragment);

  default GridSpan _gridSpan() {
    Integer colStart = colLineStart();
    assert colStart != null;
    Integer colEnd = colLineEnd();
    assert colEnd != null;
    Integer rowStart = rowLineStart();
    assert rowStart != null;
    Integer rowEnd = rowLineEnd();
    assert rowEnd != null;

    return GridSpan.create(
      colStart, colEnd - 1, rowStart, rowEnd - 1);
  }

  static GridItem create(ElementBox itemBox) {
    return new GridItemImp(itemBox);
  }
  
}
