package net.buildabrowser.babbrowser.css.engine.matcher.util;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.layer.CSSLayer;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;

public final class WeightedStyleRuleUtil {
  
  private WeightedStyleRuleUtil() {}

  public static WeightedStyleRule createWeightedRule(
    StyleRule styleRule,
    RuleSource ruleSource,
    ComplexSelector complexSelector,
    SelectorSpecificity specificity,
    CSSLayer layer,
    int sheetOrdering,
    int[] ruleOrdering
  ) {
    SelectorTarget target = determineTarget(complexSelector);
    WeightedStyleRule weightedRule = WeightedStyleRule.create(
      styleRule, specificity, target, ruleSource,
      layer, sheetOrdering, ruleOrdering[0]);
    return weightedRule;
  }

  private static SelectorTarget determineTarget(ComplexSelector complexSelector) {
    if (complexSelector.parts().size() == 0) return SelectorTarget.ELEMENT;
    SelectorPart selectorPart = getLast(complexSelector.parts());
    if (!(
      selectorPart instanceof SimplePseudoElement pseudoClass
    )) return SelectorTarget.ELEMENT;

    // TODO: Is there actually any point in mapping these?
    return switch (pseudoClass) {
      case AFTER -> SelectorTarget.AFTER;
      case BEFORE -> SelectorTarget.BEFORE;
      default -> throw new UnsupportedOperationException(
        "Unsupported psuedo class: " + pseudoClass);
    };
  }

}
