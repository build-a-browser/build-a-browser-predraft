package net.buildabrowser.babbrowser.renderer.context.imp;

import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.html.legacyattr.LegacyColorParser;
import net.buildabrowser.babbrowser.renderer.context.imp.LegacyAttribute.LegacyAttributeName;

public final class LegacyAttributeResolver {

  // TODO: Only accept legacy attrs on specific elements

  private static final SelectorSpecificity ZERO_SPECIFICITY =
    new SelectorSpecificity(0, 0, 0);
  
  // TODO: Having a singleton isn't great, but otherwise ElementContext would
  // have to hold a reference, using valuable memory
  private static Map<String, Integer> NAMED_COLORS;

  private LegacyAttributeResolver() {}

  public static LegacyAttribute resolveLegacyAttribute(
    LegacyAttributeName name, String value
  ) {
    return switch (name) {
      case LegacyAttributeName.BGCOLOR -> resolveBgColorLegacyAttribute(name, value);
      default -> throw new UnsupportedOperationException(
        "Unsupported legacy attribute name: " + name);
    };
  }

  private static LegacyAttribute resolveBgColorLegacyAttribute(
    LegacyAttributeName name, String value
  ) {
    if (NAMED_COLORS == null) {
      throw new IllegalStateException("NAMED_COLORS is a singleton that must be initialized!");
    }

    int color = LegacyColorParser.parseLegacyColor(value, NAMED_COLORS);
    if (color == -1) return null;
    ColorValue colorValue = ColorValue.fromARGB(0XFF000000 | color);
    return createLegacyAttribute(name, "background-color", colorValue);
  }

  public static void setColorMap(
    Map<String, Integer> namedColors
  ) {
    if (NAMED_COLORS != null) return;
    NAMED_COLORS = namedColors;
  }

  // TODO: I'd prefer to pass a CSSProperty over a String name
  private static LegacyAttribute createLegacyAttribute(
    LegacyAttributeName name,
    String propName,
    CSSValue value
  ) {
    StyleRule styleRule = new StyleRule(List.of(), List.of(
      // TODO: Get the actual source to pass in
      Declaration.create(null, propName, value, true)
    ));
    WeightedStyleRule weightedStyleRule = new WeightedStyleRule(
      styleRule, ZERO_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
    return new LegacyAttribute(name, weightedStyleRule);
  }

}
