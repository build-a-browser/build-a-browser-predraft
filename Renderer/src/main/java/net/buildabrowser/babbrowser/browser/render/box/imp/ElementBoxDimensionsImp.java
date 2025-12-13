package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;

public class ElementBoxDimensionsImp implements ElementBoxDimensions {
  
  private int computedWidth = 0;
  private int computedHeight = 0;

  private int preferredMinWidthConstraint = 0;
  private int preferredWidthConstraint = 0;

  @Override
  public void setComputedSize(int w, int h) {
    this.computedWidth = w;
    this.computedHeight = h;
  }

  @Override
  public int getComputedWidth() {
    return this.computedWidth;
  }

  @Override
  public int getComputedHeight() {
    return this.computedHeight;
  }

  @Override
  public void setPreferredMinWidthConstraint(int w) {
    this.preferredMinWidthConstraint = w;
  }

  @Override
  public void setPreferredWidthConstraint(int w) {
    this.preferredWidthConstraint = w;
  }

  @Override
  public int preferredMinWidthConstraint() {
    return this.preferredMinWidthConstraint;
  }

  @Override
  public int preferredWidthConstraint() {
    return this.preferredWidthConstraint;
  }
  
}
