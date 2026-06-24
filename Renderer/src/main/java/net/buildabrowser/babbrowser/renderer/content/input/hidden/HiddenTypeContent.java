package net.buildabrowser.babbrowser.renderer.content.input.hidden;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class HiddenTypeContent implements InputTypeContent {
  
  private ElementBox rootBox;

  public HiddenTypeContent(
    ElementBox rootBox
  ) {
    this.rootBox = rootBox;
  }

  @Override
  public UnmanagedBoxFragment<?> layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, 0);
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, 0);
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    UnmanagedBoxFragment<?> inputFragment = fragmentFactory.createInputBoxFragment(
      usedWidth, usedHeight, usedWidth, usedHeight,
      rootBox, this);
    rootBox.updatePositioningFragment(inputFragment);
    return inputFragment;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    rootBox.positioningFragment().setLayerPos(layerX, layerY);
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }

}
