package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridTrackImp;

public interface GridTrack {

  CSSValue sizeValue();

  void setSizeValue(CSSValue sizeValue);

  static GridTrack createExplicit() {
    return new GridTrackImp();
  }

  static GridTrack createImplicit() {
    return new GridTrackImp();
  }

}
