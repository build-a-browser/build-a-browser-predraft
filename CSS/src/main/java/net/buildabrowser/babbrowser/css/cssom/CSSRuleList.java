package net.buildabrowser.babbrowser.css.cssom;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.imp.CSSRuleListImp;

public interface CSSRuleList {
  
  CSSRule item(long index);

  long length();

  static CSSRuleList create() {
    return new CSSRuleListImp(new ArrayList<>(4));
  }

  static CSSRuleList create(List<CSSRule> rules) {
    return new CSSRuleListImp(rules);
  }

}
