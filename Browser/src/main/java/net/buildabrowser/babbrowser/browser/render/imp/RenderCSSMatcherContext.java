package net.buildabrowser.babbrowser.browser.render.imp;

import net.buildabrowser.babbrowser.browser.render.core.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class RenderCSSMatcherContext implements CSSMatcherContext {

  @Override
  public void onMatched(Node node, StyleRule matchedRule) {
    if (node instanceof MutableElement element && element.getContext() instanceof ElementContext elementContext) {
      elementContext.onCSSRuleMatched(matchedRule);
    }
  }

  @Override
  public void onUnmatched(Node node, StyleRule matchedRule) {
    if (node instanceof MutableElement element && element.getContext() instanceof ElementContext elementContext) {
      elementContext.onCSSRuleUnmatched(matchedRule);
    }
  }
  
}
