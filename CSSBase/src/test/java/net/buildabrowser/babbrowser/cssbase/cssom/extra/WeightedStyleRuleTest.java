package net.buildabrowser.babbrowser.cssbase.cssom.extra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;

public class WeightedStyleRuleTest {
  
  @Test
  @DisplayName("Author rule wins over UA rule")
  public void authorRuleWinsOverUARule() {
    WeightedStyleRule firstRule = new WeightedStyleRule(
      null, new SelectorSpecificity(0, 0, 0),
      RuleSource.AUTHOR, 0, 0);
    WeightedStyleRule secondRule = new WeightedStyleRule(
      null, new SelectorSpecificity(0, 0, 0),
      RuleSource.USER_AGENT, 0, 0);

    assertFirstHigher(firstRule, secondRule);
  }

  @Test
  @DisplayName("Rule with higher-tier specificity wins")
  public void ruleWithHigherTierSpecifityWins() {
    WeightedStyleRule firstRule = specificityRule(4, 2, 3);
    WeightedStyleRule secondRule = specificityRule(1, 4, 3);
    WeightedStyleRule thirdRule = specificityRule(1, 2, 4);
    WeightedStyleRule attrRule = specificityRule(true, 0, 0, 0);
    
    assertFirstHigher(firstRule, secondRule);
    assertFirstHigher(secondRule, thirdRule);
    assertFirstHigher(firstRule, thirdRule);
    assertFirstHigher(attrRule, firstRule);
  }

  @Test
  @DisplayName("Rule with higher specificity on same tier wins")
  public void ruleWithHigherSpecifityOnSameTierWins() {
    WeightedStyleRule firstRule = specificityRule(1, 0, 0);
    WeightedStyleRule secondRule = specificityRule(0, 0, 0);
    assertFirstHigher(firstRule, secondRule);

    firstRule = specificityRule(0, 1, 0);
    secondRule = specificityRule(0, 0, 0);
    assertFirstHigher(firstRule, secondRule);

    firstRule = specificityRule(0, 0, 1);
    secondRule = specificityRule(0, 0, 0);
    assertFirstHigher(firstRule, secondRule);
  }

  @Test
  @DisplayName("Rule with later order wins")
  public void ruleWithLaterOrderWins() {
    WeightedStyleRule firstRule = new WeightedStyleRule(
      null, new SelectorSpecificity(0, 0, 0),
      RuleSource.AUTHOR, 0, 0);
    WeightedStyleRule secondRule = new WeightedStyleRule(
      null, new SelectorSpecificity(0, 0, 0),
      RuleSource.AUTHOR, 0, 1);
    WeightedStyleRule thirdRule = new WeightedStyleRule(
      null, new SelectorSpecificity(0, 0, 0),
      RuleSource.AUTHOR, 1, 0);

    assertFirstHigher(secondRule, firstRule);
    assertFirstHigher(thirdRule, secondRule);
    assertFirstHigher(thirdRule, firstRule);
  }

  private void assertFirstHigher(WeightedStyleRule firstRule, WeightedStyleRule secondRule) {
    Assertions.assertEquals(1, WeightedStyleRule.compare(firstRule, secondRule));
    Assertions.assertEquals(-1, WeightedStyleRule.compare(secondRule, firstRule));
    Assertions.assertEquals(0, WeightedStyleRule.compare(firstRule, firstRule));
    Assertions.assertEquals(0, WeightedStyleRule.compare(secondRule, secondRule));
  }

  private WeightedStyleRule specificityRule(boolean isAttr, int a, int b, int c) {
    return new WeightedStyleRule(
      null, new SelectorSpecificity(isAttr, a, b, c),
      RuleSource.AUTHOR, 0, 0);
  }

  private WeightedStyleRule specificityRule(int a, int b, int c) {
    return specificityRule(false, a, b, c);
  }

}
