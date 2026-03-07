package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public final class FlexUtil {
  
  private FlexUtil() {}

  public static LayoutConstraint evaluateFlexBasis(
    LayoutContext layoutContext,
    ElementBox box,
    LayoutConstraint parentMainSize,
    CSSValue flexBasis,
    boolean isVertical
  ) {
    if (isVertical) {
      return SizingUtil.evaluateAdjustedHeightSize(
        layoutContext, parentMainSize, box, flexBasis);
    } else {
      return SizingUtil.evaluateAdjustedWidthSize(
        layoutContext, parentMainSize, box, flexBasis);
    }
  }

  public static LayoutConstraint boxCrossSize(
    LayoutContext layoutContext,
    ElementBox box,
    LayoutConstraint parentCrossSize,
    boolean isVertical
  ) {
    if (isVertical) {
      return SizingUtil.evaluateAdjustedWidthSize(
        layoutContext, parentCrossSize, box,
        box.activeStyles().getProperty(CSSProperty.WIDTH));
    } else {
      return SizingUtil.evaluateAdjustedHeightSize(
        layoutContext, parentCrossSize, box,
        box.activeStyles().getProperty(CSSProperty.HEIGHT));
    }
  }
  
}
