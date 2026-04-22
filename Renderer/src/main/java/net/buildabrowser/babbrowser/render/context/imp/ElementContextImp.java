package net.buildabrowser.babbrowser.render.context.imp;

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
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.context.ElementContext;

public class ElementContextImp implements ElementContext {

  private static final SelectorSpecificity ATTR_SPECIFICITY = new SelectorSpecificity(true, 0, 0, 0);

  // TreeSet has a ton of overhead, sort on access instead
  private final List<WeightedStyleRule> styleRules = new LinkedList<>();
  private final HTMLElement element;

  private ActiveStyles activeStyles = null;
  private WeightedStyleRule internalStyleRule = null;

  public ElementContextImp(HTMLElement element) {
    this.element = element;
    updateStyle(element.attributes().get("style"));
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

  private void updateStyle(String styleStr) {
    if (internalStyleRule != null) {
      onCSSRuleUnmatched(internalStyleRule);
      internalStyleRule = null;
    }

    if (styleStr != null) {
      // TODO: Might be good to find a better way to pass the CSS parser
      // For now, it is cached as a singleton
      // Also, how will !important factor into the below??
      CSSTokenizerInput tokenizerInput = CSSTokenizerInput.fromString(styleStr);
      CSSTokenStream tokenizerStream = CSSTokenStream.create(tokenizerInput);
      List<Declaration> declarations = CommonUtil.rethrow(() -> CSSParser.create().parseAStyleBlocksContents(tokenizerStream));
      // Need to do some dumb constructors to convert it to a WeightedStyleRule, maybe improve this later...
      StyleRule styleRule = new StyleRule(List.of(), declarations);
      // also why wasn't there a .create anyways?
      WeightedStyleRule weightedStyleRule = new WeightedStyleRule(styleRule, ATTR_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
      onCSSRuleMatched(weightedStyleRule);
    }
  }
  
}
