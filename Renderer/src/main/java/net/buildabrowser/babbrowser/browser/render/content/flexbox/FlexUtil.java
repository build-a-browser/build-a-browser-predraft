package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public final class FlexUtil {
  
  private FlexUtil() {}

  public static LayoutConstraint boxCrossSize(
    LayoutContext layoutContext, ElementBox box, LayoutConstraint parentCrossSize, boolean isVertical
  ) {
    CSSValue crossValue = isVertical ?
      box.activeStyles().getProperty(CSSProperty.WIDTH) :
      box.activeStyles().getProperty(CSSProperty.HEIGHT);

    return SizingUtil.evaluateBaseSize(layoutContext, parentCrossSize, crossValue);
  }
  
}
