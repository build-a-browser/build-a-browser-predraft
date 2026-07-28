package net.buildabrowser.babbrowser.renderer.content.input.checkbox;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputConstants;
import net.buildabrowser.babbrowser.renderer.content.input.InputTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class CheckBoxTypeContent implements InputTypeContent {

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, InputConstants.DEFAULT_SMALL_SIZE);
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, InputConstants.DEFAULT_SMALL_SIZE);

    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    UnmanagedBoxFragment<?> buttonFragment = fragmentFactory.createCheckBoxFragment(
      usedWidth, usedHeight, usedWidth, usedHeight,
      0, 0, rootBox);
    return buttonFragment;
  }

  @Override
  public void computeMeasures(
    ElementBox box,
    LayoutConstraint referenceConstraint
  ) {}

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    fragment.setLayerPos(layerX, layerY);
  }

}