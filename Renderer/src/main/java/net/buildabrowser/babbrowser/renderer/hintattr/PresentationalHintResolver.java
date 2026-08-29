package net.buildabrowser.babbrowser.renderer.hintattr;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint.PresentationalHintName;

public final class PresentationalHintResolver {

  // TODO: Only accept legacy attrs on specific elements

  private PresentationalHintResolver() {}

  // TODO: elName should be a qualified name
  public static PresentationalHint resolvePresentationalHint(
    String elName, PresentationalHintName name, String value
  ) {
    return switch (name) {
      case PresentationalHintName.BGCOLOR ->
        LegacyBGColorAttributeResolver.resolveBgColorLegacyAttribute(elName, name, value);
      case PresentationalHintName.WIDTH ->
        WidthAttributeResolver.resolveWidthAttribute(elName, name, value);
      case PresentationalHintName.HEIGHT ->
        HeightAttributeResolver.resolveHeightAttribute(elName, name, value);
      case PresentationalHintName.ALIGN ->
        AlignAttributeResolver.resolveAlignAttribute(elName, name, value);
      default -> throw new UnsupportedOperationException(
        "Unsupported legacy attribute name: " + name);
    };
  }

  // TODO: I'd prefer to pass a CSSProperty over a String name
  public static PresentationalHint createLegacyAttribute(
    PresentationalHintName name,
    String propName,
    CSSValue value
  ) {
    StyleRule styleRule = new StyleRule(List.of(
      // TODO: Get the actual source to pass in
      Declaration.create(null, propName, value, false)));
    WeightedStyleRule weightedStyleRule = WeightedStyleRule.create(
      styleRule, SelectorSpecificity.ZERO_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
    return new PresentationalHint(name, weightedStyleRule);
  }

}
