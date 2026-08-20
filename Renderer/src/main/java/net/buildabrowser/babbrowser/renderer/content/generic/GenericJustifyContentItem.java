package net.buildabrowser.babbrowser.renderer.content.generic;

import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public interface GenericJustifyContentItem {

  float decorMainSize(boolean isVertical);

  void setMainPos(float startPos, boolean isVertical);

  LayoutConstraint firstMargin(boolean isVertical, LayoutConstraint parentSize);

  LayoutConstraint secondMargin(boolean isVertical, LayoutConstraint parentSize);

}
