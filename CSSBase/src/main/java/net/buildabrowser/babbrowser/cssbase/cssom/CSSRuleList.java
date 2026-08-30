package net.buildabrowser.babbrowser.cssbase.cssom;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.imp.CSSRuleListImp;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.CSSRule;

public interface CSSRuleList extends Iterable<CSSRule> {
  
  CSSRule item(long index);

  long length();

  static CSSRuleList create() {
    return new CSSRuleListImp(new ArrayList<>(4));
  }

  static CSSRuleList create(List<CSSRule> rules) {
    return new CSSRuleListImp(rules);
  }

}
