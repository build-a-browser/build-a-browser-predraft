package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import static net.buildabrowser.babbrowser.css.engine.matcher.util.WeightedStyleRuleUtil.createWeightedRule;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.slot.ComplexSelectorSlot;
import net.buildabrowser.babbrowser.css.engine.matcher.slot.MediaRuleSlot;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.MediaRule;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public class CSSMatcherImp implements CSSMatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(CSSMatcherImp.class);

  private final ElementRootSet allElements;
  private final ElementSet changedElements;
  private final Set<SelectorPart> changedSelectors;
  private final CSSSelectorMatcher selectorMatcher;
  
  private final CSSMatcherContext context;
  private final StyleSheetList uaStyleSheets;
  private final SlotFamily<ComplexSelector, ComplexSelectorSlot> selectorSets;
  private final SlotFamily<MediaRule, MediaRuleSlot> mediaStates;

  public CSSMatcherImp(
    CSSMatcherContext context,
    StyleSheetList uaStyleSheets,
    SlotFamilyFamily slotFamilyFamily
  ) {
    this.context = context;
    this.uaStyleSheets = uaStyleSheets;
    this.allElements = ElementSet.createRoot();
    this.changedElements = allElements.createChild();
    this.changedSelectors = new HashSet<>();
    this.selectorSets = slotFamilyFamily.createSlotFamily(
      (_1, id) -> new ComplexSelectorSlot(allElements.root().createChild(), id));
    this.mediaStates = slotFamilyFamily.createSlotFamily(
      (_1, id) -> new MediaRuleSlot(id));
    this.selectorMatcher = new CSSSelectorMatcher(
      allElements, context, s -> changedSelectors.add(s));

    for (CSSStyleSheet styleSheet: uaStyleSheets) {
      onStylesheetAdded(styleSheet);
    }
  }

  @Override
  public void applyStylesheets(Document document, MediaContext mediaContext) {
    applyStylesheets(uaStyleSheets, RuleSource.USER_AGENT, mediaContext);
    applyStylesheets(document.styleSheets(), RuleSource.AUTHOR, mediaContext);
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
          changedElements.add(element);
        }
        super.onNodeAdded(node);
      }

      @Override
      public void onNodeRemoved(Node node) {
        if (node instanceof Element element) {
          allElements.remove(element);
        }
        super.onNodeAdded(node);
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
    switch (cssRule) {
      case StyleRule styleRule -> registerStyleRule(styleRule);
      case MediaRule mediaRule -> registerMediaRule(mediaRule);
      default -> LOGGER.warn("Ignoring unknown rule: " + cssRule);
    }
  }

  private void registerStyleRule(StyleRule styleRule) {
    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      selectorMatcher.registerSelector(complexSelector);
    }
  }

  private void registerMediaRule(MediaRule mediaRule) {
    for (CSSRule childRule: mediaRule.innerRules()) {
      registerRule(childRule);
    }
  }

  private void applyStylesheets(
    StyleSheetList stylesheets,
    RuleSource source,
    MediaContext mediaContext
  ) {
    for (int i = 0; i < stylesheets.length(); i++) {
      CSSStyleSheet styleSheet = stylesheets.item(i);
      CSSRuleList ruleList = styleSheet.cssRules();

      int[] ruleOrdering = new int[1];
      for (CSSRule rule: ruleList) {
        applyRule(mediaContext, rule, source, i, ruleOrdering, false);
      }
    }
  }

  private void applyRule(
    MediaContext mediaContext,
    CSSRule cssRule,
    RuleSource ruleSource,
    int sheetOrdering,
    int[] ruleOrdering,
    boolean forceMatch
  ) {
    switch (cssRule) {
      case StyleRule styleRule -> applyStyleRule(
        styleRule, ruleSource, sheetOrdering, ruleOrdering, forceMatch);
      case MediaRule mediaRule -> applyMediaRule(
        mediaContext, mediaRule, ruleSource,
        sheetOrdering, ruleOrdering, forceMatch);
      default -> LOGGER.warn("Ignoring unknown rule: " + cssRule);
    }
  }
    
  private void applyStyleRule(
    StyleRule styleRule,
    RuleSource ruleSource,
    int sheetOrdering,
    int[] ruleOrdering,
    boolean forceMatch
  ) {
    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      boolean needsMatched = forceMatch || needsMatched(complexSelector);
      if (!needsMatched) continue;

      WeightedStyleRule weightedRule = createWeightedRule(
        styleRule, ruleSource, complexSelector,
        selectorMatcher.computeSpecificity(complexSelector),
        sheetOrdering, ruleOrdering);

      ElementSet matchNotes = selectorSets.get(complexSelector).matchedElements();
      ElementSet matchedElements = selectorMatcher.matchElements(complexSelector);
      if (matchedElements == null) {
        for (Element element: matchNotes) {
          changedElements.add(element);
          context.onUnmatched(element, weightedRule);
        }
        continue;
      }

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

    ruleOrdering[0]++;
  }

  private void applyMediaRule(
    MediaContext mediaContext,
    MediaRule mediaRule,
    RuleSource ruleSource,
    int sheetOrdering,
    int[] ruleOrdering,
    boolean forceMatch
  ) {
    boolean isActive = mediaRule.query().resolve(mediaContext);
    MediaRuleSlot mediaState = mediaStates.get(mediaRule);
    boolean wasChanged =
      isActive != mediaState.active()
      || mediaState.ruleSize() < 0;
    
    if (!isActive && !wasChanged) {
      ruleOrdering[0] += mediaState.ruleSize();
      return;
    }

    if (isActive) {
      int startSize = ruleOrdering[0];
      for (CSSRule rule: mediaRule.innerRules()) {
        applyRule(
          mediaContext, rule, ruleSource,
          sheetOrdering, ruleOrdering,
          forceMatch || wasChanged);
      }
      mediaState.setRuleSize(ruleOrdering[0] - startSize);
    } else {
      deactivateMediaRule(mediaRule, ruleSource, sheetOrdering, ruleOrdering);
    }
    mediaState.setActive(isActive);
  }

  private void deactivateMediaRule(
    MediaRule mediaRule,
    RuleSource ruleSource,
    int sheetOrdering,
    int[] ruleOrdering
  ) {
    MediaRuleSlot mediaState = mediaStates.get(mediaRule);
    boolean needsDeactivated =
      mediaState.active()
      || mediaState.ruleSize() < 0;
    if (!needsDeactivated) {
      ruleOrdering[0] += mediaState.ruleSize();
      return;
    }

    int startSize = ruleOrdering[0];
    for (CSSRule rule: mediaRule.innerRules()) {
      switch (rule) {
        case MediaRule mediaRule2 -> deactivateMediaRule(
          mediaRule2, ruleSource, sheetOrdering, ruleOrdering);
        case StyleRule styleRule -> deactivateStyleRule(
          styleRule, ruleSource, sheetOrdering, ruleOrdering);
        default -> LOGGER.warn("Ignoring unknown rule: " + rule);
      }
    }
    mediaState.setRuleSize(ruleOrdering[0] - startSize);
  }

  private void deactivateStyleRule(
    StyleRule styleRule,
    RuleSource ruleSource,
    int sheetOrdering,
    int[] ruleOrdering
  ) {
    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      WeightedStyleRule weightedRule = createWeightedRule(
        styleRule, ruleSource, complexSelector,
        selectorMatcher.computeSpecificity(complexSelector),
        sheetOrdering, ruleOrdering);

      ElementSet matchNotes = selectorSets.get(complexSelector).matchedElements();
      for (Element element: matchNotes) {
        changedElements.add(element);
        context.onUnmatched(element, weightedRule);
        matchNotes.remove(element);
      }
    }

    ruleOrdering[0]++;
  }

  private boolean needsMatched(ComplexSelector complexSelector) {
    for (SelectorPart selectorPart: complexSelector.parts()) {
      if (changedSelectors.contains(selectorPart)) {
        return true;
      }

      if (selectorPart instanceof LogicalPseudoSelector complexPseudoSelector) {
        for (ComplexSelector subSelector: complexPseudoSelector.complexSelectors()) {
          if (needsMatched(subSelector)) {
            return true;
          }
        }
      }
    }

    return false;
  }

}
