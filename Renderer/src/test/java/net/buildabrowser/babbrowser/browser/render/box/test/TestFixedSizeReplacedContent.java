package net.buildabrowser.babbrowser.browser.render.box.test;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;

public class TestFixedSizeReplacedContent implements BoxContent {

  private final ElementBox box;
  private final int width;
  private final int height;

  public TestFixedSizeReplacedContent(ElementBox box, int width, int height) {
    this.box = box;
    this.width = width;
    this.height = height;
  }

  @Override
  public void prelayout(LayoutContext layoutContext) {
    ElementBoxDimensions dimensions = box.dimensions();
    dimensions.setPreferredMinWidthConstraint(width);
    dimensions.setPreferredWidthConstraint(width);
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    int usedWidth = LayoutUtil.constraintOrDim(widthConstraint, width);
    int usedHeight = LayoutUtil.constraintOrDim(heightConstraint, height);
    
    return new UnmanagedBoxFragment(usedWidth, usedHeight, box, null);
  }

  @Override
  public boolean isReplaced() {
    return true;
  }
  
}
