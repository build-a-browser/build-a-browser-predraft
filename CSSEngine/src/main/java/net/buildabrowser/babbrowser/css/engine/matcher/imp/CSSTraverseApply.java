package net.buildabrowser.babbrowser.css.engine.matcher.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.common.datastruct.SlotFamilyFamily;
import net.buildabrowser.babbrowser.css.engine.matcher.CSSMatcher.CSSMatcherContext;
import net.buildabrowser.babbrowser.css.engine.matcher.ElementSet;
import net.buildabrowser.babbrowser.css.engine.matcher.slot.ComplexSelectorSlot;
import net.buildabrowser.babbrowser.css.engine.matcher.slot.MediaRuleSlot;
import net.buildabrowser.babbrowser.css.engine.matcher.util.WeightedStyleRuleUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.CSSRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.LayerListRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.LayerRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.MediaRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.NestingRule;
import net.buildabrowser.babbrowser.cssbase.cssom.rule.StyleRule;
import net.buildabrowser.babbrowser.cssbase.layer.CSSLayer;
import net.buildabrowser.babbrowser.cssbase.media.MediaContext;
import net.buildabrowser.babbrowser.cssbase.selector.ComplexSelector;
import net.buildabrowser.babbrowser.cssbase.selector.LogicalPseudoSelector;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorPart;
import net.buildabrowser.babbrowser.dom.Element;

public class CSSTraverseApply {

  private static final Logger LOGGER = LoggerFactory.getLogger(CSSTraverseApply.class);

  private final CSSMatcherContext context;
  private final CSSSelectorMatcher selectorMatcher;
  private final Set<SelectorPart> changedSelectors;

  private final SlotFamily<ComplexSelector, ComplexSelectorSlot> selectorSets;
  private final SlotFamily<MediaRule, MediaRuleSlot> mediaStates;
  private final CSSLayer rootLayer;

  public CSSTraverseApply(
    CSSMatcherContext context,
    CSSSelectorMatcher selectorMatcher,
    Set<SelectorPart> changedSelectors,
    SlotFamilyFamily slotFamilyFamily,
    ElementSet allElements
  ) {
    this.context = context;
    this.selectorMatcher = selectorMatcher;
    this.changedSelectors = changedSelectors;
    this.selectorSets = slotFamilyFamily.createSlotFamily(
      (_1, id) -> new ComplexSelectorSlot(allElements.root().createChild(), id));
    this.mediaStates = slotFamilyFamily.createSlotFamily(
      (_1, id) -> new MediaRuleSlot(id));
    this.rootLayer = CSSLayer.create();
  }

  public void applyStylesheets(
    StyleSheetList stylesheets,
    RuleSource source,
    MediaContext mediaContext
  ) {
    rootLayer.resetOrder();
    for (int i = 0; i < stylesheets.length(); i++) {
      CSSStyleSheet styleSheet = stylesheets.item(i);
      CSSRuleList ruleList = styleSheet.cssRules();

      RuleContext ruleContext = new RuleContext(
        mediaContext, source, i, new int[1]);
      for (CSSRule rule: ruleList) {
        applyRule(ruleContext, rootLayer, rule, false);
      }
    }
  }

  private void applyRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    CSSRule cssRule,
    boolean forceMatch
  ) {
    switch (cssRule) {
      case StyleRule styleRule -> applyStyleRule(
        ruleContext, activeLayer, styleRule, forceMatch);
      case MediaRule mediaRule -> applyMediaRule(
        ruleContext, activeLayer, mediaRule, forceMatch);
      case LayerRule layerRule -> applyLayerRule(
        ruleContext, activeLayer, layerRule, forceMatch);
      case LayerListRule layerListRule -> applyLayerListRule(
        activeLayer, layerListRule);
      default -> LOGGER.warn("Ignoring unknown rule: " + cssRule);
    }
  }

  private void applyStyleRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    StyleRule styleRule,
    boolean forceMatch
  ) {
    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      applyStyleRuleWithSelector(ruleContext, activeLayer, styleRule, complexSelector, forceMatch);
    }
    ruleContext.ruleOrdering()[0]++;

    for (CSSRule childRule: styleRule.nestedRules()) {
      applyRule(ruleContext, activeLayer, childRule, forceMatch);
    }
  }

  private void applyStyleRuleWithSelector(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    StyleRule styleRule,
    ComplexSelector complexSelector,
    boolean forceMatch
  ) {
    boolean needsMatched = forceMatch || needsMatched(complexSelector);
    if (!needsMatched)
      return;

    WeightedStyleRule weightedRule = createWeightedRule(
      ruleContext, activeLayer, styleRule, complexSelector);

    ElementSet matchNotes = selectorSets.get(complexSelector).matchedElements();
    ElementSet matchedElements = selectorMatcher.matchElements(complexSelector);
    if (matchedElements == null) {
      for (Element element: matchNotes) {
        context.onUnmatched(element, weightedRule);
      }
      matchNotes.removeAll();
      return;
    }

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

  private void applyMediaRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    MediaRule mediaRule,
    boolean forceMatch
  ) {
    int[] ruleOrdering = ruleContext.ruleOrdering();
    boolean isActive = mediaRule.query().resolve(ruleContext.mediaContext());
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
      for (CSSRule rule: mediaRule.nestedRules()) {
        applyRule(
          ruleContext, activeLayer, rule,
          forceMatch || wasChanged);
      }
      mediaState.setRuleSize(ruleOrdering[0] - startSize);
    } else {
      deactivateMediaRule(ruleContext, activeLayer, mediaRule);
    }
    mediaState.setActive(isActive);
  }

  private void applyLayerRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    LayerRule layerRule,
    boolean forceMatch
  ) {
    CSSLayer layer = layerRule.layer();
    if (layer == null) {
      layer = createLayerWithName(activeLayer, layerRule.nameParts());
      layerRule.setLayer(layer);
    }
    markLayerUsed(layer);
    activeLayer = layer;

    for (CSSRule childRule: layerRule.nestedRules()) {
      applyRule(ruleContext, activeLayer, childRule, forceMatch);
    }
  }
    
  private void applyLayerListRule(
    CSSLayer activeLayer, LayerListRule layerListRule
  ) {
    List<CSSLayer> layers = layerListRule.layers();
    if (layers == null) {
      layers = new ArrayList<>(layerListRule.layerNames().size());
      for (List<String> layerName: layerListRule.layerNames()) {
        layers.add(createLayerWithName(activeLayer, layerName));
      }
      layerListRule.setLayers(layers);
    }

    for (CSSLayer layer: layers) {
      markLayerUsed(layer);
    }
  }

  private void deactivateRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    CSSRule rule
  ) {
    switch (rule) {
      case MediaRule mediaRule2 -> deactivateMediaRule(
        ruleContext, activeLayer, mediaRule2);
      case StyleRule styleRule -> deactivateStyleRule(
        ruleContext, activeLayer, styleRule);
      case NestingRule nestingRule -> deactivateNestingRule(
        ruleContext, activeLayer, nestingRule);
      default -> LOGGER.warn("Ignoring unknown rule: " + rule);
    }
  }

  private void deactivateMediaRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    MediaRule mediaRule
  ) {
    int[] ruleOrdering = ruleContext.ruleOrdering();
    MediaRuleSlot mediaState = mediaStates.get(mediaRule);
    boolean needsDeactivated =
      mediaState.active()
      || mediaState.ruleSize() < 1;
    if (!needsDeactivated) {
      ruleOrdering[0] += mediaState.ruleSize();
      return;
    }

    int startSize = ruleOrdering[0];
    for (CSSRule rule: mediaRule.nestedRules()) {
      deactivateRule(ruleContext, activeLayer, rule);
    }
    mediaState.setRuleSize(ruleOrdering[0] - startSize);
  }

  private void deactivateStyleRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    StyleRule styleRule
  ) {
    for (ComplexSelector complexSelector: styleRule.complexSelectors()) {
      WeightedStyleRule weightedRule = createWeightedRule(
        ruleContext, activeLayer, styleRule, complexSelector);

      ElementSet matchNotes = selectorSets.get(complexSelector).matchedElements();
      for (Element element: matchNotes) {
        context.onUnmatched(element, weightedRule);
        matchNotes.remove(element);
      }
    }

    for (CSSRule childRule: styleRule.nestedRules()) {
      deactivateRule(ruleContext, activeLayer, childRule);
    }
    
    ruleContext.ruleOrdering()[0]++;
  }

  private void deactivateNestingRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    NestingRule nestingRule
  ) {
    for (CSSRule childRule: nestingRule.nestedRules()) {
      deactivateRule(ruleContext, activeLayer, childRule);
    }
  }

  private CSSLayer createLayerWithName(
    CSSLayer activeLayer, List<String> nameParts
  ) {
    if (nameParts.isEmpty()) {
      return activeLayer.createAnonymousChild();
    }

    for (String namePart: nameParts) {
      activeLayer = activeLayer.childLayer(namePart);
    }

    return activeLayer;
  }

  private void markLayerUsed(CSSLayer layer) {
    CSSLayer currentLayer = layer;
    while (currentLayer.parentLayer() != null) {
      currentLayer.parentLayer().markChildUse(currentLayer);
      currentLayer = currentLayer.parentLayer();
    }
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

  private WeightedStyleRule createWeightedRule(
    RuleContext ruleContext,
    CSSLayer activeLayer,
    StyleRule styleRule,
    ComplexSelector complexSelector
  ) {
    return WeightedStyleRuleUtil.createWeightedRule(
      styleRule,
      ruleContext.ruleSource(),
      complexSelector,
      selectorMatcher.computeSpecificity(complexSelector),
      activeLayer,
      ruleContext.sheetOrdering(),
      ruleContext.ruleOrdering);
  }

  private static record RuleContext(
    MediaContext mediaContext,
    RuleSource ruleSource,
    int sheetOrdering,
    int[] ruleOrdering
  ) {}
  
}
