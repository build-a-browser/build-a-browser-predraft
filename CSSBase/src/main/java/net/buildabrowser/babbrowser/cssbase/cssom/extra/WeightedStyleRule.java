package net.buildabrowser.babbrowser.cssbase.cssom.extra;

import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;

public record WeightedStyleRule(
  StyleRule rule,
  SelectorSpecificity specificity,
  SelectorTarget target,
  RuleSource ruleSource,
  int sheetOrdering,
  int ruleOrdering
) {
  
  public static int compare(WeightedStyleRule a, WeightedStyleRule b) {
    int specificity = SelectorSpecificity.compare(a.specificity(), b.specificity());

    return
      a.ruleSource().ordinal() > b.ruleSource.ordinal() ? 1 :
      a.ruleSource().ordinal() < b.ruleSource.ordinal() ? -1 :
      specificity != 0 ? specificity :
      a.sheetOrdering() > b.sheetOrdering() ? 1 :
      a.sheetOrdering() < b.sheetOrdering() ? -1 :
      a.ruleOrdering() > b.ruleOrdering() ? 1 :
      a.ruleOrdering() < b.ruleOrdering() ? -1 :
      0;
  }

  public static enum RuleSource {
    USER_AGENT, AUTHOR
  }

  public static WeightedStyleRule create(
    StyleRule rule,
    SelectorSpecificity specificity,
    SelectorTarget target,
    RuleSource ruleSource,
    int sheetOrdering,
    int ruleOrdering
  ) {
    return new WeightedStyleRule(
      rule, specificity, target,
      ruleSource, sheetOrdering, ruleOrdering);
  }

  public static WeightedStyleRule create(
    StyleRule rule,
    SelectorSpecificity specificity,
    RuleSource ruleSource,
    int sheetOrdering,
    int ruleOrdering
  ) {
    return new WeightedStyleRule(
      rule, specificity, SelectorTarget.ELEMENT,
      ruleSource, sheetOrdering, ruleOrdering);
  }

}
