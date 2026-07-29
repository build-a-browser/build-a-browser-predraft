package net.buildabrowser.babbrowser.renderer.content.input;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class InstrinsicSizedInputTypeContent implements InputTypeContent {

  // TODO: Support field-sizing
  // TODO: Support size attribute
  @Override
  public void computeIntrinsics(ElementBox rootBox) {
    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float intrinsicWidth = convertACharacterWidthToPixels(fontMetrics, 20);
    float intrinsicHeight = fontMetrics.height(); // TODO: Use line-height instead
    rootBox.alterDimensions(false, dimensions -> {
      dimensions.setIntrinsicWidth(intrinsicWidth);
      dimensions.setInstrinsicHeight(intrinsicHeight);
    });
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = rootBox.dimensions();
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, dimensions.intrinsicWidth());
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, dimensions.intrinsicHeight());
    float inkWidth = Math.max(usedWidth, dimensions.intrinsicWidth());
    float inkHeight = Math.max(usedWidth, dimensions.intrinsicHeight());
    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float lastBaseline = fontMetrics.descent();
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    return fragmentFactory.createInputBoxFragment(
      usedWidth, usedHeight, inkWidth, inkHeight,
      0, lastBaseline, // TODO: Compute baselines
      rootBox, this);
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    fragment.setLayerPos(layerX, layerY);
  }

  public static float convertACharacterWidthToPixels(FontMetrics fontMetrics, int size) {
    // TODO: Proper way to determine avg and max
    float avg = fontMetrics.stringWidth("a");
    float max = fontMetrics.stringWidth("W");
    return (size - 1) * avg + max;
  }
  
}
