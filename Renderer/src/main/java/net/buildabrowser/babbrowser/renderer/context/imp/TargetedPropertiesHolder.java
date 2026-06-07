package net.buildabrowser.babbrowser.renderer.context.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;

public class TargetedPropertiesHolder implements IntrusiveList<TargetedPropertiesHolder> {

  private final List<WeightedStyleRule> matchedRules = new ArrayList<>(1);
  private final SelectorTarget target;

  private PropertyContainer container;
  private TargetedPropertiesHolder next;

  public TargetedPropertiesHolder(SelectorTarget target) {
    this.target = target;
  }

  public SelectorTarget target() {
    return this.target;
  }

  public PropertyContainer container() {
    return this.container;
  }

  public void matchRule(WeightedStyleRule rule) {
    matchedRules.add(rule);
  }

  public void unmatchRule(WeightedStyleRule rule) {
    matchedRules.remove(rule);
  }

  public List<WeightedStyleRule> matchedRules() {
    matchedRules.sort(WeightedStyleRule::compare);
    return matchedRules;
  }

  @Override
  public TargetedPropertiesHolder next() {
    return this.next;
  }

  @Override
  public void setNext(TargetedPropertiesHolder next) {
    this.next = next;
  }

  public void setContainer(PropertyContainer container) {
    this.container = container;
  }
  
}
