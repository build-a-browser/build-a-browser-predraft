package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentItem;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericTrack;
import net.buildabrowser.babbrowser.renderer.content.grid.imp.GridTrackImp;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public interface GridTrack extends GenericJustifyContentItem, GenericTrack {

  CSSValue minTrackSizingFunction();

  CSSValue maxTrackSizingFunction();

  void setSizeValue(CSSValue sizeValue);

  CSSValue _sizeValue();

  // TODO: Maybe convert to a float to avoid having to assert everywhere
  LayoutConstraint baseSize();

  void setBaseSize(LayoutConstraint baseSize);

  LayoutConstraint growthLimit();

  void setGrowthLimit(LayoutConstraint growthLimit);

  void setFrozen(boolean frozen);

  boolean frozen();

  //

  void increaseItemIncurredIncrease(float increase);

  float itemIncurredIncrease();

  void finalizeItemIncurredIncrease();

  boolean hasPlannedIncrease();

  float plannedIncrease();

  boolean isInfinitelyGrowable();

  void setInfinitelyGrowable(boolean isInfinitelyGrowable);

  //

  float position();

  void setPosition(float position);

  //

  static GridTrack createExplicit() {
    return new GridTrackImp();
  }

  static GridTrack createImplicit() {
    return new GridTrackImp();
  }

}
