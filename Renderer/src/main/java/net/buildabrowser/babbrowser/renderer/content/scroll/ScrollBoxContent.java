package net.buildabrowser.babbrowser.renderer.content.scroll;

import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.GUTTER_WIDTH;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class ScrollBoxContent implements BoxContent {

  @Override
  public void computeIntrinsics(ElementBox rootBox) {
    ElementBox innerBox = (ElementBox) rootBox.childBoxes().next();
    BoxContent innerContent = innerBox.content();
    innerContent.computeIntrinsics(innerBox);
  }

  @Override
  public ScrollBoxFragment layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    // TODO: This algorithm could have exponential runtime for nested scroll containers.
    // Hopefully the cache helps enough...
    ElementBox innerBox = (ElementBox) rootBox.childBoxes().next();
    ElementBoxDimensions dimensions = innerBox.dimensions();
    BoxContent innerContent = innerBox.content();
    boolean isPreLayout = widthConstraint.isPreLayoutConstraint() || heightConstraint.isPreLayoutConstraint();

    LayoutConstraint adjustedWidthConstraint = widthConstraint;
    if (
      adjustedWidthConstraint.type().equals(LayoutConstraintType.AUTO)
      && dimensions.intrinsicWidth() != -1
    ) {
      // This is a fix for textarea scrolling when scroll is auto
      // TODO: A bit hacky, does it interfere with other cases?
      adjustedWidthConstraint = LayoutConstraint.of(dimensions.intrinsicWidth());
    }

    LayoutConstraint adjustedHeightConstraint = heightConstraint;
    if (
      adjustedHeightConstraint.type().equals(LayoutConstraintType.AUTO)
      && dimensions.intrinsicHeight() != -1
    ) {
      adjustedHeightConstraint = LayoutConstraint.of(dimensions.intrinsicHeight());
    }

    boolean addedHorizontalScrollbars = false;
    boolean addedVerticalScrollbars = false;

    UnmanagedBoxFragment<?> innerLayout = innerContent.layout(
      innerBox, adjustedWidthConstraint, adjustedHeightConstraint);

    if (needXScrollbars(rootBox, innerLayout, adjustedWidthConstraint)) {
      adjustedHeightConstraint = subtractGutterWidth(adjustedHeightConstraint);
      addedHorizontalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(
          innerBox, adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }

    if (needYScrollbars(rootBox, innerLayout, adjustedHeightConstraint)) {
      adjustedWidthConstraint = subtractGutterWidth(adjustedWidthConstraint);
      addedVerticalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(
          innerBox, adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }
    
    if (
      !addedHorizontalScrollbars
      && needXScrollbars(rootBox, innerLayout, adjustedWidthConstraint)
    ) {
      adjustedHeightConstraint = subtractGutterWidth(adjustedHeightConstraint);
      addedHorizontalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(
          innerBox, adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }

    float inkWidth = innerLayout.inkWidth(Measurement.CONTENT) + (addedVerticalScrollbars ? GUTTER_WIDTH : 0);
    float inkHeight = innerLayout.inkHeight(Measurement.CONTENT) + (addedHorizontalScrollbars ? GUTTER_WIDTH : 0);
    float outerWidth = dimensions.intrinsicWidth() != -1 ?
      dimensions.intrinsicWidth() : inkWidth;
    float outerHeight = dimensions.intrinsicHeight() != -1 ?
      dimensions.intrinsicHeight() : inkHeight;
    float usedWidth = LayoutUtil.clampedUsedWidth(rootBox, widthConstraint, outerWidth);
    float usedHeight = LayoutUtil.clampedUsedHeight(rootBox, heightConstraint, outerHeight);

    innerLayout.setPos(0, 0);
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    ScrollBoxFragment scrollBoxFragment = fragmentFactory.createScrollBoxFragment(
      usedWidth, usedHeight,
      inkWidth, inkHeight,
      addedHorizontalScrollbars,
      addedVerticalScrollbars,
      (ScrollBox) rootBox, innerLayout);
    scrollBoxFragment.setLayerPos(0, 0);
    // TODO: I believe this gets replaced by a PosRefBox on one site, I don't recall which
    // Does that break things?
    rootBox.updatePositioningFragment(scrollBoxFragment);
    return scrollBoxFragment;
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    ScrollBoxFragment scrollBox = (ScrollBoxFragment) fragment;
    ElementBox childBox = (ElementBox) scrollBox.box().childBoxes().next();

    float contentOffsetX = fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.PADDING);
    float contentOffsetY = fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.PADDING);

    childBox.content().positionLayers(
      scrollBox.innerFragment(), contentOffsetX, contentOffsetY);
  }

  @Override
  public boolean hasCustomContent(ElementBox box) {
    return true;
  }

  private static LayoutConstraint subtractGutterWidth(LayoutConstraint origConstraint) {
    if (!origConstraint.isBounded()) return origConstraint;
    return LayoutConstraint.of(Math.max(0, origConstraint.value() - GUTTER_WIDTH));
  }

  private static boolean needXScrollbars(
    ElementBox box,
    UnmanagedBoxFragment<?> innerLayout,
    LayoutConstraint adjustedWidthConstraint
  ) {
    ElementBoxDimensions dimensions = innerLayout.box().dimensions();
    float intrinsicWidth = dimensions.intrinsicWidth();
    CSSValue overflowX = box.properties().get(CSSProperty.OVERFLOW_X);
    if (overflowX.equals(OverflowValue.SCROLL)) return true;
    if (
      !adjustedWidthConstraint.isBounded()
      && intrinsicWidth == -1
    ) return false;
    if (!overflowX.equals(OverflowValue.AUTO)) return false;
    float compWidth = LayoutUtil.constraintOrDim(adjustedWidthConstraint, intrinsicWidth);
    return compWidth < innerLayout.inkWidth(Measurement.CONTENT);
  }

  private static boolean needYScrollbars(
    ElementBox box,
    UnmanagedBoxFragment<?> innerLayout,
    LayoutConstraint adjustedHeightConstraint
  ) {
    ElementBoxDimensions dimensions = innerLayout.box().dimensions();
    float intrinsicHeight = dimensions.intrinsicHeight();
    CSSValue overflowY = box.properties().get(CSSProperty.OVERFLOW_Y);
    if (overflowY.equals(OverflowValue.SCROLL)) return true;
    if (
      !adjustedHeightConstraint.isBounded()
      && intrinsicHeight == -1
    ) return false;
    if (!overflowY.equals(OverflowValue.AUTO)) return false;
    float compHeight = LayoutUtil.constraintOrDim(adjustedHeightConstraint, intrinsicHeight);
    return compHeight < innerLayout.inkHeight(Measurement.CONTENT);
  }
  
}
