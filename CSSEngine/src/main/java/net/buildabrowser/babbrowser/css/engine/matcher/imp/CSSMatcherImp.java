package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.HashSet;
import java.util.Set;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.NestingRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class CSSMatcherImp implements CSSMatcher {

  private final ElementRootSet allElements;
  private final Set<SelectorPart> changedSelectors;
  private final CSSSelectorMatcher selectorMatcher;
  private final CSSTraverseApply traverseApply;
  private final StyleSheetList uaStyleSheets;

  public CSSMatcherImp(
    CSSMatcherContext context,
    StyleSheetList uaStyleSheets,
    SlotFamilyFamily slotFamilyFamily
  ) {
    this.uaStyleSheets = uaStyleSheets;
    this.allElements = ElementSet.createRoot();
    this.changedSelectors = new HashSet<>();
    this.selectorMatcher = new CSSSelectorMatcher(
      allElements, context, s -> changedSelectors.add(s));
    this.traverseApply = new CSSTraverseApply(
      context, selectorMatcher, changedSelectors,
      slotFamilyFamily, allElements);

    for (CSSStyleSheet styleSheet: uaStyleSheets) {
      onStylesheetAdded(styleSheet);
    }
  }

  @Override
  public void applyStylesheets(Document document, MediaContext mediaContext) {
    traverseApply.applyStylesheets(uaStyleSheets, RuleSource.USER_AGENT, mediaContext);
    traverseApply.applyStylesheets(document.styleSheets(), RuleSource.AUTHOR, mediaContext);
    changedSelectors.clear();
  }

  @Override
  public DocumentChangeListener documentChangeListener() {
    return new AbstractDocumentChangeListener(selectorMatcher.documentChangeListener()) {
      @Override
      public void onStylesheetAdded(CSSStyleSheet styleSheet) {
        CSSMatcherImp.this.onStylesheetAdded(styleSheet);
      }

      @Override
      public void onNodeAdded(Node node) {
        if (node instanceof Element element) {
          allElements.add(element);
        }
        super.onNodeAdded(node);
      }

      @Override
      public void onNodeRemoved(Node node) {
        if (node instanceof Element element) {
          allElements.remove(element);
        }
        super.onNodeRemoved(node);
      }
    };
  }

  @Override
  public boolean changed() {
    return !changedSelectors.isEmpty();
  }

  @Override
  public ElementRootSet allElements() {
    return this.allElements;
  }

  private void onStylesheetAdded(CSSStyleSheet styleSheet) {
    CSSRuleList ruleList = styleSheet.cssRules();
    for (int j = 0; j < ruleList.length(); j++) {
      registerRule(ruleList.item(j));
    }
  }

  private void registerRule(CSSRule cssRule) {
    if (cssRule instanceof StyleRule styleRule) {
      for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
        selectorMatcher.registerSelector(complexSelector);
      }
    }

    if (cssRule instanceof NestingRule nestingRule) {
      for (CSSRule childRule: nestingRule.nestedRules()) {
        registerRule(childRule);
      }
    }
  }

}
