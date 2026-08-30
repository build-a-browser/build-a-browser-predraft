package net.buildabrowser.babbrowser.cssbase.cssom.extra;

import java.util.Objects;

import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.layer.CSSLayer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;

public record WeightedStyleRule(
  StyleRule rule,
  SelectorSpecificity specificity,
  SelectorTarget target,
  RuleSource ruleSource,
  CSSLayer layer,
  int sheetOrdering,
  int ruleOrdering,
  // Hash is precomputed to improve performance
  int hashCodeO
) {
  
  public static int compare(WeightedStyleRule a, WeightedStyleRule b) {
    return compare(a, b, false);
  }

  public static int compareImportant(WeightedStyleRule a, WeightedStyleRule b) {
    return compare(a, b, true);
  }

  private static int compare(WeightedStyleRule a, WeightedStyleRule b, boolean isImportant) {
    int specificity = SelectorSpecificity.compare(a.specificity(), b.specificity());
    int layerOrder = CSSLayer.compareOrder(a.layer(), b.layer());
    if (isImportant) {
      layerOrder *= -1;
    }

    return
      a.ruleSource().ordinal() > b.ruleSource.ordinal() ? 1 :
      a.ruleSource().ordinal() < b.ruleSource.ordinal() ? -1 :
      layerOrder != 0 ? layerOrder :
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
    CSSLayer layer,
    int sheetOrdering,
    int ruleOrdering
  ) {
    int hash = Objects.hash(
      rule, specificity, target,
      ruleSource, layer, sheetOrdering, ruleOrdering);
    return new WeightedStyleRule(
      rule, specificity, target,
      ruleSource, layer,
      sheetOrdering, ruleOrdering, hash);
  }

  public static WeightedStyleRule create(
    StyleRule rule,
    SelectorSpecificity specificity,
    RuleSource ruleSource,
    int sheetOrdering,
    int ruleOrdering
  ) {
    return create(
      rule, specificity, SelectorTarget.ELEMENT,
      ruleSource, null, sheetOrdering, ruleOrdering);
  }

}
