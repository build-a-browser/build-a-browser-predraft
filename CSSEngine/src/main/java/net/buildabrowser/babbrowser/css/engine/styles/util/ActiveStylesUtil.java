package net.buildabrowser.babbrowser.css.engine.styles.util;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;

public final class ActiveStylesUtil {
  
  private ActiveStylesUtil() {}

  public static int textColor(ActiveStyles activeStyles) {
    return ((ColorValue) activeStyles.getProperty(CSSProperty.COLOR)).asSARGB();
  }

  public static int backgroundColor(ActiveStyles activeStyles) {
    return ((ColorValue) activeStyles.getProperty(CSSProperty.BACKGROUND_COLOR)).asSARGB();
  }

  public static OuterDisplayValue outerDisplayValue(ActiveStyles activeStyles) {
    return ((DisplayValue) activeStyles.getProperty(CSSProperty.DISPLAY)).outerDisplayValue();
  }

  public static InnerDisplayValue innerDisplayValue(ActiveStyles activeStyles) {
    return ((DisplayValue) activeStyles.getProperty(CSSProperty.DISPLAY)).innerDisplayValue();
  }

}
