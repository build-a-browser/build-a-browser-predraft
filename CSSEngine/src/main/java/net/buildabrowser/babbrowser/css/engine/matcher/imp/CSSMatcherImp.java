package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import static net.buildabrowser.babbrowser.css.engine.matcher.util.WeightedStyleRuleUtil.createWeightedRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementRootSet;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.pseudo.PseudoSelectorMatchers;
import net.buildabrowser.babbrowser.css.engine.matcher.simple.SimpleSelectorMatchers;
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
import net.buildabrowser.babbrowser.cssbase.selector.ChildCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.Combinator;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.DescendantCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.NextSiblingCombinator;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoElement;
import net.buildabrowser.babbrowser.cssbase.selector.SimplePseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SimpleSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SubsequentSiblingCombinator;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.ForkedDocumentChangeListener;

public class CSSMatcherImp implements CSSMatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(CSSMatcherImp.class);

  private final ElementRootSet allElements;
  private final ElementSet changedElements;
  private final Set<SelectorPart> changedSelectors;
  private final SimpleSelectorMatchers simpleMatchers;
  private final PseudoSelectorMatchers pseudoMatchers;
  private final CombinatorMatchers combinatorMatchers;
  
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
    this.simpleMatchers = new SimpleSelectorMatchers(allElements, s -> changedSelectors.add(s));
    this.pseudoMatchers = new PseudoSelectorMatchers(
      allElements, s -> changedSelectors.add(s), context);
    this.combinatorMatchers = new CombinatorMatchers(allElements);
    this.selectorSets = slotFamilyFamily.createSlotFamily(
      (_1, id) -> new ComplexSelectorSlot(allElements.root().createChild(), id));
    this.mediaStates = slotFamilyFamily.createSlotFamily(
      (_1, id) -> new MediaRuleSlot(id));

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
    return new ForkedDocumentChangeListener(
      simpleMatchers, pseudoMatchers
    ) {
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
      for (SelectorPart selectorPart: complexSelector.parts()) {
        switch (selectorPart) {
          case SimpleSelector simpleSelector -> simpleMatchers.addSelectorReference(simpleSelector);
          case SimplePseudoSelector simplePseudoSelector -> pseudoMatchers.addSelectorReference(simplePseudoSelector);
          case SimplePseudoElement _1 -> {}
          case Combinator _1 -> {}
          default -> throw new UnsupportedOperationException(
            "Unrecognized selector type: " + selectorPart);
        }
      }
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
        sheetOrdering, ruleOrdering);

      ElementSet matchNotes = selectorSets.get(complexSelector).matchedElements();
      ElementSet matchedElements = matchElements(complexSelector);
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
    }

    return false;
  }

  private ElementSet matchElements(ComplexSelector complexSelector) {
    List<SelectorPart> parts = complexSelector.parts();
    if (parts.size() == 0) return null;
    ElementSet currentMatched = match(parts.get(0)).copy();
    for (int i = 1; i < parts.size(); i++) {
      SelectorPart part = parts.get(i);
      if (part instanceof Combinator combinator) {
        ElementSet nextMatched = match(parts.get(++i));
        currentMatched = matchCombinator(combinator, currentMatched, nextMatched);
      } else if (part instanceof SimplePseudoElement) {
        if (i != parts.size() - 1) return null;
      } else {
        currentMatched.intersect(match(part));
      }
    }

    return currentMatched;
  }

  private ElementSet match(SelectorPart selectorPart) {
    return switch (selectorPart) {
      case SimpleSelector simpleSelector -> simpleMatchers.match(simpleSelector);
      case SimplePseudoSelector simplePseudoSelector -> pseudoMatchers.match(simplePseudoSelector);
      case SimplePseudoElement _1 -> allElements;
      default -> throw new UnsupportedOperationException(
        "Unrecognized selector type: " + selectorPart);
    };
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
