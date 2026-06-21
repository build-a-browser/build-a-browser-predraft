package net.buildabrowser.babbrowser.renderer.content.input;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class InstrinsicSizedInputTypeContent implements InputTypeContent {

  private ElementBox rootBox;

  public InstrinsicSizedInputTypeContent(
    ElementBox rootBox
  ) {
    this.rootBox = rootBox;
  }

  // TODO: Support field-sizing
  // TODO: Support size attribute
  @Override
  public void computeIntrinsics() {
    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float intrinsicWidth = convertACharacterWidthToPixels(fontMetrics, 20);
    float intrinsicHeight = fontMetrics.height(); // TODO: Use line-height instead
    System.out.println(intrinsicWidth + " " + intrinsicHeight);
    rootBox.alterDimensions(false, dimensions -> {
      dimensions.setIntrinsicWidth(intrinsicWidth);
      dimensions.setInstrinsicHeight(intrinsicHeight);
    });
  }

  @Override
  public UnmanagedBoxFragment<?> layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint) {
    ElementBoxDimensions dimensions = rootBox.dimensions();
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, dimensions.intrinsicWidth());
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, dimensions.intrinsicHeight());
    float inkWidth = Math.max(usedWidth, dimensions.intrinsicWidth());
    float inkHeight = Math.max(usedWidth, dimensions.intrinsicHeight());
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    UnmanagedBoxFragment<?> inputFragment = fragmentFactory.createInputBoxFragment(
      usedWidth, usedHeight, inkWidth, inkHeight,
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

  private float convertACharacterWidthToPixels(FontMetrics fontMetrics, int size) {
    // TODO: Proper way to determine avg and max
    float avg = fontMetrics.stringWidth("a");
    float max = fontMetrics.stringWidth("W");
    return (size - 1) * avg + max;
  }
  
}
