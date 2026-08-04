package net.buildabrowser.babbrowser.renderer.content.input.hidden;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class HiddenTypeContent implements InputTypeContent {
  
  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    float usedWidth = LayoutUtil.clampedUsedWidth(rootBox, widthConstraint, 0);
    float usedHeight = LayoutUtil.clampedUsedHeight(rootBox, heightConstraint, 0);
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createInputBoxFragment(
      usedWidth, usedHeight, usedWidth, usedHeight,
      0, 0, // TODO: Compute baselines
      rootBox, this);
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    fragment.setLayerPos(layerX, layerY);
  }

}
