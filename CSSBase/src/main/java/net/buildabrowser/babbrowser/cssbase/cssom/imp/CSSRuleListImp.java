package net.buildabrowser.babbrowser.cssbase.cssom.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;

public record CSSRuleListImp(List<CSSRule> rules) implements CSSRuleList {

  @Override
  public CSSRule item(long index) {
    assert index <= Integer.MAX_VALUE && index >= 0;
    return rules.get((int) index);
  }

  @Override
  public long length() {
    return rules.size();
  }
  
}
