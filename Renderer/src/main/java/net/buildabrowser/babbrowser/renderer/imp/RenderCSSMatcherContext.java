package net.buildabrowser.babbrowser.renderer.imp;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public class RenderCSSMatcherContext implements CSSMatcherContext {

  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public RenderCSSMatcherContext(
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    this.renderContexts = renderContexts;
  }

  @Override
  public void onMatched(Node node, WeightedStyleRule matchedRule) {
    if (node instanceof HTMLElement element) {
      if (renderContexts.get(element) instanceof ElementContext elementContext) {
        elementContext.onCSSRuleMatched(matchedRule);
      }
    }
  }

  @Override
  public void onUnmatched(Node node, WeightedStyleRule matchedRule) {
    if (node instanceof HTMLElement element) {
      if (renderContexts.get(element) instanceof ElementContext elementContext) {
        elementContext.onCSSRuleUnmatched(matchedRule);
      }
    }
  }

  @Override
  public boolean isFocusVisible(Element element) {
    return
      element.nodeDocument() instanceof HTMLDocument htmlDocument
      && htmlDocument.focusManager().focusOptions().focusVisible;
  }
  
}
