package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.renderer.content.grid.GridTrack;

public class GridTrackImp implements GridTrack {

  private CSSValue sizeValue = CSSValue.AUTO;

  @Override
  public CSSValue sizeValue() {
    return this.sizeValue;
  }

  @Override
  public void setSizeValue(CSSValue sizeValue) {
    this.sizeValue = sizeValue;
  }
  
}
