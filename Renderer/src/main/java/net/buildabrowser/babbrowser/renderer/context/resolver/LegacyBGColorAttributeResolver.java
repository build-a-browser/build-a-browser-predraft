package net.buildabrowser.babbrowser.renderer.context.resolver;

import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.html.legacyattr.LegacyColorParser;
import net.buildabrowser.babbrowser.renderer.context.resolver.LegacyAttribute.LegacyAttributeName;

public final class LegacyBGColorAttributeResolver {

  private static final Set<String> ALLOWED_ELEMENTS = Set.of(
    "body", "table", "thead", "tbody", "tfoot", "tr", "td", "th");
  
  // TODO: Having a singleton isn't great, but otherwise ElementContext would
  // have to hold a reference, using valuable memory
  private static Map<String, Integer> NAMED_COLORS;
  
  private LegacyBGColorAttributeResolver() {}

  public static LegacyAttribute resolveBgColorLegacyAttribute(
    String elName, LegacyAttributeName name, String value
  ) {
    if (NAMED_COLORS == null) {
      throw new IllegalStateException("NAMED_COLORS is a singleton that must be initialized!");
    }
    if (!(ALLOWED_ELEMENTS.contains(elName))) return null;

    int color = LegacyColorParser.parseLegacyColor(value, NAMED_COLORS);
    if (color == -1) return null;
    ColorValue colorValue = ColorValue.fromARGB(0XFF000000 | color);
    return LegacyAttributeResolver.createLegacyAttribute(name, "background-color", colorValue);
  }

  public static void setColorMap(
    Map<String, Integer> namedColors
  ) {
    if (NAMED_COLORS != null) return;
    NAMED_COLORS = namedColors;
  }

}
