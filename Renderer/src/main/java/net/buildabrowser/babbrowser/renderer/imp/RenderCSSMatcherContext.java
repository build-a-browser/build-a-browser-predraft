package net.buildabrowser.babbrowser.renderer.imp;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class RenderCSSMatcherContext implements CSSMatcherContext {

  @Override
  public void onMatched(Node node, WeightedStyleRule matchedRule) {
    if (node instanceof HTMLElement element && element.getContext() instanceof ElementContext elementContext) {
      elementContext.onCSSRuleMatched(matchedRule);
    }
  }

  @Override
  public void onUnmatched(Node node, WeightedStyleRule matchedRule) {
    if (node instanceof HTMLElement element && element.getContext() instanceof ElementContext elementContext) {
      elementContext.onCSSRuleUnmatched(matchedRule);
    }
  }
  
}
