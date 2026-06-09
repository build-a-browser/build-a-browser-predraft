package net.buildabrowser.babbrowser.renderer.content.scroll;

import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.GUTTER_WIDTH;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class ScrollBoxContent implements BoxContent {

  private final ScrollBox rootBox;

  public ScrollBoxContent(ScrollBox scrollBox) {
    this.rootBox = scrollBox;
  }

  @Override
  public ScrollBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    // TODO: This algorithm could have exponential runtime for nested scroll containers.
    // Hopefully the cache helps enough...
    LayoutConstraint adjustedWidthConstraint = widthConstraint;
    LayoutConstraint adjustedHeightConstraint = heightConstraint;
    boolean addedHorizontalScrollbars = false;
    boolean addedVerticalScrollbars = false;

    BoxContent innerContent = ((ElementBox) rootBox.childBoxes().next()).content();
    boolean isPreLayout = widthConstraint.isPreLayoutConstraint() || heightConstraint.isPreLayoutConstraint();
    UnmanagedBoxFragment<?> innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);

    if (needXScrollbars(rootBox, innerLayout, adjustedWidthConstraint)) {
      adjustedHeightConstraint = subtractGutterWidth(adjustedHeightConstraint);
      addedHorizontalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }

    if (needYScrollbars(rootBox, innerLayout, adjustedHeightConstraint)) {
      adjustedWidthConstraint = subtractGutterWidth(adjustedWidthConstraint);
      addedVerticalScrollbars = true;
      if (!isPreLayout) {
        innerLayout = innerContent.layout(adjustedWidthConstraint, adjustedHeightConstraint);
      }
    }
    
    if (
      !addedHorizontalScrollbars
      && needXScrollbars(rootBox, innerLayout, adjustedWidthConstraint)
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
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    ScrollBoxFragment scrollBoxFragment = fragmentFactory.createScrollBoxFragment(
      usedWidth, usedHeight,
      outerWidth, outerHeight,
      addedHorizontalScrollbars,
      addedVerticalScrollbars,
      rootBox, innerLayout);
    scrollBoxFragment.setLayerPos(0, 0);
    // TODO: I believe this gets replaced by a PosRefBox on one site, I don't recall which
    // Does that break things?
    rootBox.updatePositioningFragment(scrollBoxFragment);
    return scrollBoxFragment;
  }

  @Override
  public void positionLayers(float layerX, float layerY) {
    ((ElementBox) rootBox.childBoxes().next()).content().positionLayers(0, 0);
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
    CSSValue overflowX = box.properties().get(CSSProperty.OVERFLOW_X);
    overflowX = CompositeLayerUtil.adjustOverflowValueIfHTML(box.element(), overflowX, CSSProperty.OVERFLOW_X);
    if (overflowX.equals(OverflowValue.SCROLL)) return true;
    if (!adjustedWidthConstraint.isBounded()) return false;
    if (!overflowX.equals(OverflowValue.AUTO)) return false;
    return adjustedWidthConstraint.value() < innerLayout.inkWidth(Measurement.CONTENT);
  }

  private static boolean needYScrollbars(
    ElementBox box,
    UnmanagedBoxFragment<?> innerLayout,
    LayoutConstraint adjustedHeightConstraint
  ) {
    CSSValue overflowY = box.properties().get(CSSProperty.OVERFLOW_Y);
    overflowY = CompositeLayerUtil.adjustOverflowValueIfHTML(box.element(), overflowY, CSSProperty.OVERFLOW_Y);
    if (overflowY.equals(OverflowValue.SCROLL)) return true;
    if (!adjustedHeightConstraint.isBounded()) return false;
    if (!overflowY.equals(OverflowValue.AUTO)) return false;
    return adjustedHeightConstraint.value() < innerLayout.inkHeight(Measurement.CONTENT);
  }

  @Override
  public ElementBox rootBox() {
    return this.rootBox;
  }
  
}
