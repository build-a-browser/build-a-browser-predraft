package net.buildabrowser.babbrowser.render.context;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.render.context.imp.ElementContextImp;

public interface ElementContext {
  
  void onCSSRuleMatched(WeightedStyleRule matchedRule);

  void onCSSRuleUnmatched(WeightedStyleRule matchedRule);

  void onAttributeValueChanged(String attrName, String oldValue, String newValue);

  ActiveStyles activeStyles();

  static ElementContext create(Element element) {
    return new ElementContextImp(element);
  }

}
