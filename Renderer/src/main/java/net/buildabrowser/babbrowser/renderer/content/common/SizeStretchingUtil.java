package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class SizeStretchingUtil {
  
  private SizeStretchingUtil() {}

  public static SizeStretchResult stretch(
    LayoutConstraint parentConstraint, ElementBox childBox,
    float extraLeftMargin, float extraRightMargin
  ) {
    LayoutConstraint marginStartConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentConstraint,
      childBox.properties().get(CSSProperty.MARGIN_LEFT));
    LayoutConstraint marginEndConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentConstraint,
      childBox.properties().get(CSSProperty.MARGIN_RIGHT));

    boolean isStartMarginSet = marginStartConstraint.isBounded();
    boolean isEndMarginSet = marginEndConstraint.isBounded();
    float usedStartMargin = isStartMarginSet ? marginStartConstraint.value() : 0;
    usedStartMargin = Math.max(usedStartMargin, extraLeftMargin);
    float usedEndMargin = isEndMarginSet ? marginEndConstraint.value() : 0;
    usedEndMargin = Math.max(usedEndMargin, extraRightMargin);

    ElementBoxDimensions boxDimensions = childBox.dimensions();
    float[] border = boxDimensions.getComputedBorder();
    float[] padding = boxDimensions.getComputedPadding();

    float autoWidth = parentConstraint.value()
      - usedStartMargin - usedEndMargin
      - border[2] - border[3] - padding[2] - padding[3];

    LayoutConstraint stretchConstraint = parentConstraint.isBounded() ?
      LayoutConstraint.of(autoWidth) : parentConstraint;
    
    // TODO: This allocation is not great
    return new SizeStretchResult(
      stretchConstraint,
      isStartMarginSet,
      isEndMarginSet,
      usedStartMargin,
      usedEndMargin,
      border[2], border[3],
      padding[2], padding[3]);
  }

  public static record SizeStretchResult(
    LayoutConstraint stretchConstraint,
    boolean isStartMarginSet,
    boolean isEndMarginSet,
    float computedStartMargin,
    float computedEndMargin,
    float startBorder,
    float endBorder,
    float startPadding,
    float endPadding
  ) {

    public float decorSize(Measurement measurement) {
      return switch (measurement) {
        case MARGIN -> decorSize(Measurement.BORDER) + computedStartMargin + computedEndMargin;
        case BORDER -> decorSize(Measurement.PADDING) + startBorder + endBorder;
        case PADDING -> decorSize(Measurement.CONTENT) + startPadding + endPadding;
        case CONTENT -> 0;
        default -> throw new UnsupportedOperationException(
          "Unrecognized measurement: " + measurement);
      };
    }

  }

}
