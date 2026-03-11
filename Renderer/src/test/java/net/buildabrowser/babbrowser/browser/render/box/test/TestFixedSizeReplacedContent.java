package net.buildabrowser.babbrowser.browser.render.box.test;

import net.buildabrowser.babbrowser.browser.render.box.BoxContent;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;

public class TestFixedSizeReplacedContent implements BoxContent {

  private final ElementBox box;
  private final float width;
  private final float height;

  public TestFixedSizeReplacedContent(ElementBox box, float width, float height) {
    this.box = box;
    this.width = width;
    this.height = height;
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, width);
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, height);
    
    return new UnmanagedBoxFragment(usedWidth, usedHeight, box, null);
  }

  @Override
  public boolean isReplaced() {
    return true;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    
  }
  
}
