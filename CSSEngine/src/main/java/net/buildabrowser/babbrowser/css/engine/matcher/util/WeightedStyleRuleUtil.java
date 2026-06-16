package net.buildabrowser.babbrowser.css.engine.matcher.util;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;

public final class WeightedStyleRuleUtil {
  
  private WeightedStyleRuleUtil() {}

  public static WeightedStyleRule createWeightedRule(
    StyleRule styleRule,
    RuleSource ruleSource,
    ComplexSelector complexSelector,
    int sheetOrdering,
    int[] ruleOrdering
  ) {
    SelectorSpecificity specificity = computeSpecificity(complexSelector);
    SelectorTarget target = determineTarget(complexSelector);
    WeightedStyleRule weightedRule = WeightedStyleRule.create(
      styleRule, specificity, target,
      ruleSource, sheetOrdering, ruleOrdering[0]);
    return weightedRule;
  }

  private static SelectorSpecificity computeSpecificity(ComplexSelector selector) {
    int numIdSelectors = 0;
    int numClassSelectors = 0;
    int numTypeSelectors = 0;
    for (SelectorPart selectorPart: selector.parts()) {
      switch (selectorPart) {
        case IdSelector _1 -> numIdSelectors++;
        case AttributeSelector _1 -> numClassSelectors++;
        case TypeSelector _1 -> numTypeSelectors++;
        default -> {}
      }
    }

    return new SelectorSpecificity(numIdSelectors, numClassSelectors, numTypeSelectors);
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
