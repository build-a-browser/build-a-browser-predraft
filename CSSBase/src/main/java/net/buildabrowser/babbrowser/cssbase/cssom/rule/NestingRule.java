package net.buildabrowser.babbrowser.cssbase.cssom.rule;

import java.util.List;

public interface NestingRule extends CSSRule {
  
  List<CSSRule> nestedRules();

}
