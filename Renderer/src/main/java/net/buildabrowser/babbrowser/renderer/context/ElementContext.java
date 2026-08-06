package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.imp.ElementContextImp;

public interface ElementContext extends RenderContext {
  
  void onCSSRuleMatched(WeightedStyleRule matchedRule);

  void onCSSRuleUnmatched(WeightedStyleRule matchedRule);

  void onAttributeValueChanged(String attrName, String oldValue, String newValue);
  

  void setBox(ElementBox box);

  InvalidationLevel invalidationLevel();

  static ElementContext create(
    HTMLElement element, short familyId
  ) {
    return new ElementContextImp(element, familyId);
  }

}
