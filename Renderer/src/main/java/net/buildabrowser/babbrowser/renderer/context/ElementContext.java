package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.imp.ElementContextImp;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public interface ElementContext extends Invalidatable, SlotItem<ElementContext> {
  
  void onCSSRuleMatched(WeightedStyleRule matchedRule);

  void onCSSRuleUnmatched(WeightedStyleRule matchedRule);

  void onAttributeValueChanged(String attrName, String oldValue, String newValue);

  void regenerateStyles(StyleCache styleCache);

  PropertyContainer properties();

  PropertyContainer targetedProperties(SelectorTarget target);

  HTMLElement element();

  void setBox(ElementBox box);

  ElementBox box();

  InvalidationLevel invalidationLevel();

  static ElementContext create(
    HTMLElement element, short familyId
  ) {
    return new ElementContextImp(element, familyId);
  }

}
