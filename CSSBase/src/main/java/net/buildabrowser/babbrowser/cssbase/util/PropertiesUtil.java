package net.buildabrowser.babbrowser.cssbase.util;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;

public final class PropertiesUtil {
  
  private PropertiesUtil() {}

  public static int textColor(PropertyContainer properties) {
    return ((ColorValue) properties.get(CSSProperty.COLOR)).asSARGB();
  }

  public static int backgroundColor(PropertyContainer properties) {
    return ((ColorValue) properties.get(CSSProperty.BACKGROUND_COLOR)).asSARGB();
  }

  public static OuterDisplayValue outerDisplayValue(PropertyContainer properties) {
    return ((DisplayValue) properties.get(CSSProperty.DISPLAY)).outerDisplayValue();
  }

  public static InnerDisplayValue innerDisplayValue(PropertyContainer properties) {
    return ((DisplayValue) properties.get(CSSProperty.DISPLAY)).innerDisplayValue();
  }

}
