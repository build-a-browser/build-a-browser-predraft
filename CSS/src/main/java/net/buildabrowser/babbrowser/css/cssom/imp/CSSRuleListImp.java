package net.buildabrowser.babbrowser.css.cssom.imp;

import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.CSSRule;
import net.buildabrowser.babbrowser.css.cssom.CSSRuleList;

public record CSSRuleListImp(List<CSSRule> rules) implements CSSRuleList {

  @Override
  public CSSRule item(long index) {
    assert index <= Integer.MAX_VALUE && index > 0;
    return rules.get((int) index);
  }

  @Override
  public long length() {
    return rules.size();
  }
  
}
