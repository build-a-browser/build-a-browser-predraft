package net.buildabrowser.babbrowser.renderer.box.test;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.test.TestUnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

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
  public UnmanagedBoxFragment<?> layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, width);
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, height);
    
    return new TestUnmanagedBoxFragment(0, 0, usedWidth, usedHeight, box);
  }

  @Override
  public boolean isReplaced() {
    return true;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    
  }

  @Override
  public ElementBox rootBox() {
    return this.box;
  }
  
}
