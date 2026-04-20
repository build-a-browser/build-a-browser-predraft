package net.buildabrowser.babbrowser.render.composite.imp.scroll;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutUtil;

public final class ScrollLayoutUtil {

  public static final int GUTTER_WIDTH = 16;

  private ScrollLayoutUtil() {}

  public static ScrollBoxFragment layoutScrollable(
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint,
    ElementBox box
  ) {
    // TODO: This algorithm could have exponential runtime for nested scroll containers.
    // Hopefully the cache helps enough...
    LayoutConstraint adjustedWidthConstraint = widthConstraint;
    LayoutConstraint adjustedHeightConstraint = heightConstraint;
    boolean addedHorizontalScrollbars = false;
    boolean addedVerticalScrollbars = false;

    BoxContent innerContent = box.content();
    boolean isPreLayout = widthConstraint.isPreLayoutConstraint() || heightConstraint.isPreLayoutConstraint();
    UnmanagedBoxFragment innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);

    if (needXScrollbars(box, innerLayout, adjustedWidthConstraint)) {
      adjustedHeightConstraint = subtractGutterWidth(adjustedHeightConstraint);
      addedHorizontalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }

    if (needYScrollbars(box, innerLayout, adjustedHeightConstraint)) {
      adjustedWidthConstraint = subtractGutterWidth(adjustedWidthConstraint);
      addedVerticalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }
    
    if (
      !addedHorizontalScrollbars
      && needXScrollbars(box, innerLayout, adjustedWidthConstraint)
    ) {
      adjustedHeightConstraint = subtractGutterWidth(adjustedHeightConstraint);
      addedHorizontalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }

    float outerWidth = innerLayout.inkWidth(Measurement.CONTENT) + (addedVerticalScrollbars ? GUTTER_WIDTH : 0);
    float outerHeight = innerLayout.inkHeight(Measurement.CONTENT) + (addedHorizontalScrollbars ? GUTTER_WIDTH : 0);
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, outerWidth);
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, outerHeight);

    innerLayout.setPos(0, 0);
    return new ScrollBoxFragment(
      usedWidth, usedHeight,
      outerWidth, outerHeight,
      addedHorizontalScrollbars,
      addedVerticalScrollbars,
      innerLayout.box(),
      innerLayout);
  }

  private static LayoutConstraint subtractGutterWidth(LayoutConstraint origConstraint) {
    if (!origConstraint.isBounded()) return origConstraint;
    return LayoutConstraint.of(Math.max(0, origConstraint.value() - GUTTER_WIDTH));
  }

  private static boolean needXScrollbars(
    ElementBox box,
    UnmanagedBoxFragment innerLayout,
    LayoutConstraint adjustedWidthConstraint
  ) {
    CSSValue overflowX = box.activeStyles().getProperty(CSSProperty.OVERFLOW_X);
    overflowX = CompositeLayerUtil.adjustOverflowValueIfHTML(box.element(), overflowX, CSSProperty.OVERFLOW_X);
    if (overflowX.equals(OverflowValue.SCROLL)) return true;
    if (!adjustedWidthConstraint.isBounded()) return false;
    if (!overflowX.equals(OverflowValue.AUTO)) return false;
    return adjustedWidthConstraint.value() < innerLayout.inkWidth(Measurement.CONTENT);
  }

  private static boolean needYScrollbars(
    ElementBox box,
    UnmanagedBoxFragment innerLayout,
    LayoutConstraint adjustedHeightConstraint
  ) {
    CSSValue overflowY = box.activeStyles().getProperty(CSSProperty.OVERFLOW_Y);
    overflowY = CompositeLayerUtil.adjustOverflowValueIfHTML(box.element(), overflowY, CSSProperty.OVERFLOW_Y);
    if (overflowY.equals(OverflowValue.SCROLL)) return true;
    if (!adjustedHeightConstraint.isBounded()) return false;
    if (!overflowY.equals(OverflowValue.AUTO)) return false;
    return adjustedHeightConstraint.value() < innerLayout.inkHeight(Measurement.CONTENT);
  }
  
}
