package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.imp.ElementContextImp;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public interface ElementContext {
  
  void onCSSRuleMatched(WeightedStyleRule matchedRule);

  void onCSSRuleUnmatched(WeightedStyleRule matchedRule);

  void onAttributeValueChanged(String attrName, String oldValue, String newValue);

  void regenerateStyles(StyleCache styleCache);

  PropertyContainer properties();

  static ElementContext create(HTMLElement element) {
    return new ElementContextImp(element);
  }

}
