package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.List;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.simple.SimpleSelectorMatchers;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.selector.AttributeSelector;
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.IdSelector;
import net.buildabrowser.babbrowser.cssbase.selector.NextSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.selector.SubsequentSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class CSSMatcherImp implements CSSMatcher {

  private final SimpleSelectorMatchers matchers = new SimpleSelectorMatchers(
    () -> this.changed = true);
  
  private final CSSMatcherContext context;

  private boolean changed = false;

  public CSSMatcherImp(CSSMatcherContext context) {
    this.context = context;
  }

  @Override
  public void applyStylesheets(Document document, StyleSheetList uaStyleSheets) {
    applyStylesheets(uaStyleSheets, RuleSource.USER_AGENT);
    applyStylesheets(document.styleSheets(), RuleSource.AUTHOR);
    this.changed = false;
  }

  @Override
  public DocumentChangeListener documentChangeListener() {
    return new AbstractDocumentChangeListener(matchers) {

      @Override
      public void onStylesheetAdded(CSSStyleSheet styleSheet) {
        CSSRuleList ruleList = styleSheet.cssRules();
        for (int j = 0; j < ruleList.length(); j++) {
          registerRule(ruleList.item(j));
        }
      }
      
    };    
  }

  @Override
  public boolean changed() {
    return this.changed;
  }

  private void registerRule(CSSRule cssRule) {
    if (!(cssRule instanceof StyleRule styleRule)) return;

    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      for (SelectorPart selectorPart: complexSelector.parts()) {
        matchers.addSelectorReference(selectorPart);
      }
    }
  }

  private void applyStylesheets(StyleSheetList stylesheets, RuleSource source) {
    for (int i = 0; i < stylesheets.length(); i++) {
      CSSStyleSheet styleSheet = stylesheets.item(i);
      CSSRuleList ruleList = styleSheet.cssRules();
      for (int j = 0; j < ruleList.length(); j++) {
        registerRule(ruleList.item(j));
        applyRule(ruleList.item(j), source, i, j);
      }
    }
  }

  private void applyRule(
    CSSRule cssRule,
    RuleSource ruleSource,
    int sheetOrdering,
    int ruleOrdering
  ) {
    if (!(cssRule instanceof StyleRule styleRule)) return;
    
    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      SelectorSpecificity specificity = computeSpecificity(complexSelector);
      WeightedStyleRule weightedRule = new WeightedStyleRule(
        styleRule, specificity, ruleSource, sheetOrdering, ruleOrdering);
      
      ElementSet matchedElements = matchElements(complexSelector);
      if (matchedElements == null) {
        if (complexSelector.dataSlot() != null) {
          for (Element element: (ElementSet) complexSelector.dataSlot()) {
            context.onUnmatched(element, weightedRule);
          }
        }
        continue;
      }

      if (complexSelector.dataSlot() == null) {
        complexSelector.setDataSlot(matchedElements.root().createUntrackedChild());
      }

      ElementSet matchNotes = (ElementSet) complexSelector.dataSlot();
      for (Element element: matchNotes) {
        if (!(matchedElements.contains(element))) {
          context.onUnmatched(element, weightedRule);
          matchNotes.remove(element);
        }
      }

      for (Element element: matchedElements) {
        if (matchNotes.contains(element)) continue;
        context.onMatched(element, weightedRule);
        matchNotes.add(element);
      }
    }
  }

  private SelectorSpecificity computeSpecificity(ComplexSelector selector) {
    int numIdSelectors = 0;
    int numClassSelectors = 0;
    int numTypeSelectors = 0;
    for (SelectorPart selectorPart: selector.parts()) {
      switch (selectorPart) {
        case IdSelector _ -> numIdSelectors++;
        case AttributeSelector _ -> numClassSelectors++;
        case TypeSelector _ -> numTypeSelectors++;
        default -> {}
      }
    }

    return new SelectorSpecificity(numIdSelectors, numClassSelectors, numTypeSelectors);
  }

  private ElementSet matchElements(ComplexSelector complexSelector) {
    List<SelectorPart> parts = complexSelector.parts();
    if (parts.size() == 0) return null;
    ElementSet currentMatched = matchers.match(parts.get(0)).copy();
    for (int i = 1; i < parts.size(); i++) {
      SelectorPart part = parts.get(i);
      if (part instanceof Combinator combinator) {
        ElementSet nextMatched = matchers.match(parts.get(++i));
        currentMatched = matchCombinator(combinator, currentMatched, nextMatched);
      } else {
        currentMatched.intersect(matchers.match(part));
      }
    }

    return currentMatched;
  }

  private ElementSet matchCombinator(Combinator combinator, ElementSet currentMatched, ElementSet nextMatched) {
    return switch (combinator) {
      case DescendantCombinator _ -> CombinatorMatchers.matchDescendants(currentMatched, nextMatched);
      case ChildCombinator _ -> CombinatorMatchers.matchChild(currentMatched, nextMatched);
      case NextSiblingCombinator _ -> CombinatorMatchers.matchNextSibling(currentMatched, nextMatched);
      case SubsequentSiblingCombinator _ -> CombinatorMatchers.matchSubsequentSibling(currentMatched, nextMatched);
      default -> throw new IllegalArgumentException();
    };
  }

}
