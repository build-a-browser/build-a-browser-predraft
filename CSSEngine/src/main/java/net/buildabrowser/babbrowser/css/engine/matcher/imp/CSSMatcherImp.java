package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
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
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.SubsequentSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.TypeSelector;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class CSSMatcherImp implements CSSMatcher {

  private final ElementRootSet allElements;
  private final ElementSet changedElements;
  private final Set<SelectorPart> changedSelectors;
  private final SimpleSelectorMatchers matchers;
  private final CombinatorMatchers combinatorMatchers;
  
  private final CSSMatcherContext context;
  private final StyleSheetList uaStyleSheets;

  public CSSMatcherImp(CSSMatcherContext context, StyleSheetList uaStyleSheets) {
    this.context = context;
    this.uaStyleSheets = uaStyleSheets;
    this.allElements = ElementSet.createRoot();
    this.changedElements = allElements.createChild();
    this.changedSelectors = new HashSet<>();
    this.matchers = new SimpleSelectorMatchers(allElements, s -> changedSelectors.add(s));
    this.combinatorMatchers = new CombinatorMatchers(allElements);

    for (CSSStyleSheet styleSheet: uaStyleSheets) {
      onStylesheetAdded(styleSheet);
    }
  }

  @Override
  public void applyStylesheets(Document document) {
    applyStylesheets(uaStyleSheets, RuleSource.USER_AGENT);
    applyStylesheets(document.styleSheets(), RuleSource.AUTHOR);
    changedSelectors.clear();
  }

  @Override
  public DocumentChangeListener documentChangeListener() {
    return new AbstractDocumentChangeListener(matchers) {
      @Override
      public void onStylesheetAdded(CSSStyleSheet styleSheet) {
        CSSMatcherImp.this.onStylesheetAdded(styleSheet);
      }

      public void onNodeAdded(Node node) {
        super.onNodeAdded(node);
        if (node instanceof Element element) {
          changedElements.add(element);
        }
      }
    };
  }

  @Override
  public boolean changed() {
    return !changedSelectors.isEmpty();
  }

  @Override
  public ElementSet changedElements() {
    ElementSet changed = this.changedElements.copy();
    this.changedElements.removeAll();
    return changed;
  }

  private void onStylesheetAdded(CSSStyleSheet styleSheet) {
    CSSRuleList ruleList = styleSheet.cssRules();
    for (int j = 0; j < ruleList.length(); j++) {
      registerRule(ruleList.item(j));
    }
  }

  private void registerRule(CSSRule cssRule) {
    if (!(cssRule instanceof StyleRule styleRule)) return;

    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      for (SelectorPart selectorPart: complexSelector.parts()) {
        if (selectorPart instanceof SimplePseudoElement) continue;
        matchers.addSelectorReference(selectorPart);
      }
    }
  }

  private void applyStylesheets(StyleSheetList stylesheets, RuleSource source) {
    for (int i = 0; i < stylesheets.length(); i++) {
      CSSStyleSheet styleSheet = stylesheets.item(i);
      CSSRuleList ruleList = styleSheet.cssRules();
      for (int j = 0; j < ruleList.length(); j++) {
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
      boolean needsMatched = needsMatched(complexSelector);
      if (!needsMatched) continue;

      SelectorSpecificity specificity = computeSpecificity(complexSelector);
      SelectorTarget target = determineTarget(complexSelector);
      WeightedStyleRule weightedRule = WeightedStyleRule.create(
        styleRule, specificity, target,
        ruleSource, sheetOrdering, ruleOrdering);

      ElementSet matchedElements = matchElements(complexSelector);
      if (matchedElements == null) {
        if (complexSelector.dataSlot() != null) {
          for (Element element: (ElementSet) complexSelector.dataSlot()) {
            changedElements.add(element);
            context.onUnmatched(element, weightedRule);
          }
        }
        continue;
      }

      if (complexSelector.dataSlot() == null) {
        complexSelector.setDataSlot(matchedElements.root().createChild());
      }

      ElementSet matchNotes = (ElementSet) complexSelector.dataSlot();
      for (Element element: matchNotes) {
        if (!(matchedElements.contains(element))) {
          changedElements.add(element);
          context.onUnmatched(element, weightedRule);
          matchNotes.remove(element);
        }
      }

      for (Element element: matchedElements) {
        if (matchNotes.contains(element)) continue;
        changedElements.add(element);
        context.onMatched(element, weightedRule);
        matchNotes.add(element);
      }
    }
  }

  private boolean needsMatched(ComplexSelector complexSelector) {
    for (SelectorPart selectorPart: complexSelector.parts()) {
      if (changedSelectors.contains(selectorPart)) {
        return true;
      }
    }

    return false;
  }

  private SelectorSpecificity computeSpecificity(ComplexSelector selector) {
    int numIdSelectors = 0;
    int numClassSelectors = 0;
    int numTypeSelectors = 0;
    for (SelectorPart selectorPart: selector.parts()) {
      switch (selectorPart) {
        case IdSelector _1 -> numIdSelectors++;
        case AttributeSelector _1 -> numClassSelectors++;
        case TypeSelector _1 -> numTypeSelectors++;
        default -> {}
      }
    }

    return new SelectorSpecificity(numIdSelectors, numClassSelectors, numTypeSelectors);
  }

  private SelectorTarget determineTarget(ComplexSelector complexSelector) {
    if (complexSelector.parts().size() == 0) return SelectorTarget.ELEMENT;
    SelectorPart selectorPart = getLast(complexSelector.parts());
    if (!(
      selectorPart instanceof SimplePseudoElement pseudoClass
    )) return SelectorTarget.ELEMENT;

    // TODO: Is there actually any point in mapping these?
    return switch (pseudoClass) {
      case AFTER -> SelectorTarget.AFTER;
      case BEFORE -> SelectorTarget.BEFORE;
      default -> throw new UnsupportedOperationException(
        "Unsupported psuedo class: " + pseudoClass);
    };
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
      } else if (part instanceof SimplePseudoElement) {
        if (i != parts.size() - 1) return null;
      } else {
        currentMatched.intersect(matchers.match(part));
      }
    }

    return currentMatched;
  }

  private ElementSet matchCombinator(Combinator combinator, ElementSet currentMatched, ElementSet nextMatched) {
    return switch (combinator) {
      case DescendantCombinator _1 -> combinatorMatchers.matchDescendants(currentMatched, nextMatched);
      case ChildCombinator _1 -> combinatorMatchers.matchChild(currentMatched, nextMatched);
      case NextSiblingCombinator _1 -> combinatorMatchers.matchNextSibling(currentMatched, nextMatched);
      case SubsequentSiblingCombinator _1 -> combinatorMatchers.matchSubsequentSibling(currentMatched, nextMatched);
      default -> throw new IllegalArgumentException();
    };
  }

}
