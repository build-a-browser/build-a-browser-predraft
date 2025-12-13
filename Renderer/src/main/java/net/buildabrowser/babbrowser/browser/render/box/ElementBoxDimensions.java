package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.box.imp.ElementBoxDimensionsImp;

public interface ElementBoxDimensions {

  void setComputedSize(int w, int h);

  int getComputedWidth();

  int getComputedHeight();

  void setPreferredMinWidthConstraint(int w);

  void setPreferredWidthConstraint(int w);

  int preferredMinWidthConstraint();

  int preferredWidthConstraint();

  static ElementBoxDimensions create() {
    return new ElementBoxDimensionsImp();
  }
  
}
