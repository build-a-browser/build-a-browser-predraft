package net.buildabrowser.babbrowser.css.cssom;

import net.buildabrowser.babbrowser.css.cssom.imp.CSSStyleSheetImp;

public interface CSSStyleSheet {
  
  CSSRuleList cssRules();

  static CSSStyleSheet create(CSSRuleList rules) {
    return new CSSStyleSheetImp(rules);
  }

}
