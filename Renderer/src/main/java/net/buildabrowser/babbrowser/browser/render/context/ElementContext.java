package net.buildabrowser.babbrowser.browser.render.context;

import net.buildabrowser.babbrowser.browser.render.context.imp.ElementContextImp;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public interface ElementContext {
  
  void onCSSRuleMatched(WeightedStyleRule matchedRule);

  void onCSSRuleUnmatched(WeightedStyleRule matchedRule);

  void onAttributeValueChanged(String attrName, String oldValue, String newValue);

  ActiveStyles activeStyles();

  static ElementContext create(MutableElement element) {
    return new ElementContextImp(element);
  }

}
