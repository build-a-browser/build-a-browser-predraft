package net.buildabrowser.babbrowser.renderer.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class RenderCSSMatcherContext implements CSSMatcherContext {

  private final SlotFamily<HTMLElement, ElementContext> elementContexts;

  public RenderCSSMatcherContext(
    SlotFamily<HTMLElement, ElementContext> elementContexts
  ) {
    this.elementContexts = elementContexts;
  }

  @Override
  public void onMatched(Node node, WeightedStyleRule matchedRule) {
    if (node instanceof HTMLElement element) {
      ElementContext elementContext = elementContexts.get(element);
      elementContext.onCSSRuleMatched(matchedRule);
    }
  }

  @Override
  public void onUnmatched(Node node, WeightedStyleRule matchedRule) {
    if (node instanceof HTMLElement element) {
      ElementContext elementContext = elementContexts.get(element);
      elementContext.onCSSRuleUnmatched(matchedRule);
    }
  }
  
}
