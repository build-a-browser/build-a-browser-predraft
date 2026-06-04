package net.buildabrowser.babbrowser.renderer.context.resolver;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.renderer.context.resolver.LegacyAttribute.LegacyAttributeName;

public final class LegacyAttributeResolver {

  // TODO: Only accept legacy attrs on specific elements

  private static final SelectorSpecificity ZERO_SPECIFICITY =
    new SelectorSpecificity(0, 0, 0);

  private LegacyAttributeResolver() {}

  // TODO: elName should be a qualified name
  public static LegacyAttribute resolveLegacyAttribute(
    String elName, LegacyAttributeName name, String value
  ) {
    return switch (name) {
      case LegacyAttributeName.BGCOLOR ->
        LegacyBGColorAttributeResolver.resolveBgColorLegacyAttribute(elName, name, value);
      default -> throw new UnsupportedOperationException(
        "Unsupported legacy attribute name: " + name);
    };
  }

  // TODO: I'd prefer to pass a CSSProperty over a String name
  public static LegacyAttribute createLegacyAttribute(
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
