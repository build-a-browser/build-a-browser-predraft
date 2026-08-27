package net.buildabrowser.babbrowser.renderer.content.generic;

import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public interface GenericJustifyContentItem {

  float mainSize();

  void setMainPos(float startPos);

  LayoutConstraint firstMargin(LayoutConstraint parentSize);

  LayoutConstraint secondMargin(LayoutConstraint parentSize);

}
