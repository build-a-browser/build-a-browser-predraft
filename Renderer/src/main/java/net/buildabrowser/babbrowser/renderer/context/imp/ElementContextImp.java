package net.buildabrowser.babbrowser.renderer.context.imp;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesGenerator;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.Document;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.context.imp.LegacyAttribute.LegacyAttributeName;

public class ElementContextImp implements ElementContext {

  private static final SelectorSpecificity ATTR_SPECIFICITY = new SelectorSpecificity(true, 0, 0, 0);

  // TreeSet has a ton of overhead, sort on access instead
  private final List<WeightedStyleRule> styleRules = new LinkedList<>();
  private final HTMLElement element;
  // TODO: Remove the need for this field

  private ActiveStyles activeStyles = null;
  private WeightedStyleRule internalStyleRule = null;
  private LegacyAttribute legacyAttributes = null;

  public ElementContextImp(HTMLElement element) {
    this.element = element;
    updateStyle(element.getAttribute("style"));
    // TODO: More efficient iterator (but it does not exist yet)
    for (String attribute: element.getAttributeNames()) {
      onAttributeValueChanged(attribute, null, element.getAttribute(attribute));
    }
  }
  
  @Override
  public void onCSSRuleMatched(WeightedStyleRule styleRule) {
    // TODO: Could cause exponential growth as the list grows large..
    styleRules.add(styleRule);
  }

  @Override
  public void onCSSRuleUnmatched(WeightedStyleRule styleRule) {
    styleRules.remove(styleRule);
  }

  // TODO: Use a qualified name instead
  @Override
  public void onAttributeValueChanged(String attrName, String oldValue, String newValue) {
    if (attrName.equals("style")) {
      updateStyle(newValue);
    }

    updateLegacyAttrs(attrName, newValue);
  }

  @Override
  public void regenerateStyles() {
    ActiveStyles oldStyles = this.activeStyles;
    ActiveStyles parentStyles = element.parentNode() instanceof HTMLElement parent ?
      ((ElementContext) parent.getContext()).activeStyles() :
      null;
    styleRules.sort(WeightedStyleRule::compare);
    this.activeStyles = ActiveStylesGenerator.generateActiveStyles(styleRules, parentStyles);

    if (oldStyles == null) {
      element.invalidate(InvalidationLevel.BOX);
    } else {
      // TODO: This is an inefficient way to do this, but we can't put a change listener on the
      //   ActiveStyles since it is regenerated from scratch (to make sure selector specificity,
      //   vars, etc. are respected each render)
      for (CSSProperty property : CSSProperty.values()) {
        if (property.hasExpansion()) continue;
        if (!activeStyles.getProperty(property).equals(oldStyles.getProperty(property))) {
          element.invalidate(property.invalidationLevel());
        }
      }
    }
  }

  @Override
  public ActiveStyles activeStyles() {
    assert this.activeStyles != null;
    return this.activeStyles;
  }

  private void updateLegacyAttrs(String attrName, String newValue) {
    LegacyAttributeName legacyAttrName = LegacyAttribute.lookup(attrName);
    if (legacyAttrName == null) return;
    removeLegacyAttribute(legacyAttrName);

    LegacyAttribute newAttribute = LegacyAttributeResolver.resolveLegacyAttribute(
      legacyAttrName, newValue);
    if (newAttribute == null) return;
    onCSSRuleMatched(newAttribute.rule());
    newAttribute.setNext(legacyAttributes);
    this.legacyAttributes = newAttribute;
  }

  private void removeLegacyAttribute(LegacyAttributeName legacyAttrName) {
    if (legacyAttributes == null) return;
    if (legacyAttributes.name().equals(legacyAttrName)) {
      this.legacyAttributes = legacyAttributes.next();
      return;
    }

    LegacyAttribute prevAttr = legacyAttributes;
    LegacyAttribute currentAttr = legacyAttributes.next();
    while (currentAttr != null) {
      if (currentAttr.name().equals(legacyAttrName)) {
        prevAttr.setNext(currentAttr.next());
        currentAttr.setNext(null);
        onCSSRuleUnmatched(currentAttr.rule());
        return;
      }

      prevAttr = prevAttr.next();
      currentAttr = currentAttr.next();
    }
  }

  private void updateStyle(String styleStr) {
    if (internalStyleRule != null) {
      onCSSRuleUnmatched(internalStyleRule);
      internalStyleRule = null;
    }

    if (styleStr != null) {
      // TODO: Might be good to find a better way to pass the CSS parser
      // For now, it is cached as a singleton
      // Also, how will !important factor into the below??
      Document nodeDocument = element.nodeDocument();
      URI baseURL = nodeDocument instanceof HTMLDocument htmlDocument ? htmlDocument.baseURL() : nodeDocument.url();
      CSSTokenStreamSource source = new CSSTokenStreamSource(baseURL);
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(styleStr);
      CSSTokenStream tokenizerStream = CSSTokenStream.create(source, tokenizerInput);
      List<Declaration> declarations = CommonUtil.rethrow(
        () -> CSSParser.create().parseAStyleBlocksContents(tokenizerStream));
      // Need to do some dumb constructors to convert it to a WeightedStyleRule, maybe improve this later...
      StyleRule styleRule = new StyleRule(List.of(), declarations);
      // also why wasn't there a .create anyways?
      WeightedStyleRule weightedStyleRule = new WeightedStyleRule(styleRule, ATTR_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
      onCSSRuleMatched(weightedStyleRule);
    }
  }
  
}
