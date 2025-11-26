package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.DocumentChangeListener;

public class CSSMatcherImp implements CSSMatcher {

  private final Map<String, Set<Element>> elementsByType = new HashMap<>();
  
  private final CSSMatcherContext context;

  public CSSMatcherImp(CSSMatcherContext context) {
    this.context = context;
  }

  @Override
  public void applyStylesheets(Document document) {
    StyleSheetList stylesheets = document.styleSheets();
    for (int i = 0; i < stylesheets.length(); i++) {
      CSSStyleSheet styleSheet = stylesheets.item(i);
      CSSRuleList ruleList = styleSheet.cssRules();
      for (int j = 0; j < ruleList.length(); j++) {
        applyRule(ruleList.item(j));
      }
    }
  }

  @Override
  public DocumentChangeListener documentChangeListener() {
    return new DocumentChangeListener() {

      @Override
      public void onNodeAdded(Node node) {
        switch (node) {
          case Element element -> onElementAdded(element);
          default -> {}
        }
      }

      @Override
      public void onNodeRemoved(Node node) {
        switch (node) {
          case Element element -> onElementRemoved(element);
          default -> {}
        }
      }
      
    };    
  }

  private void applyRule(CSSRule cssRule) {
    // TODO Other selector types
    if (!(cssRule instanceof StyleRule styleRule)) return;
    TypeSelector typeSelector = (TypeSelector) styleRule.complexSelectors().get(0).parts().get(0);
    for (Element element: elementsByType.getOrDefault(typeSelector.tagName(), Set.of())) {
      context.onMatched(element, styleRule);
    }
  }
  
  private void onElementAdded(Element element) {
    elementsByType
      .computeIfAbsent(element.name(), _ -> new HashSet<>())
      .add(element);
  }

  private void onElementRemoved(Element element) {
    Set<Element> elSet = elementsByType.get(element.name());
    if (elSet == null) return;

    elSet.remove(element);
    if (elSet.isEmpty()) {
      elementsByType.remove(element.name());
    }
  }

}
