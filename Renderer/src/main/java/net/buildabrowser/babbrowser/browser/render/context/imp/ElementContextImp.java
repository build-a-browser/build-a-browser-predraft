package net.buildabrowser.babbrowser.browser.render.context.imp;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.buildabrowser.babbrowser.browser.render.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesGenerator;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule.RuleSource;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorSpecificity;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.utils.CommonUtils;

public class ElementContextImp implements ElementContext {

  private static final SelectorSpecificity ATTR_SPECIFICITY = new SelectorSpecificity(true, 0, 0, 0);

  private final Set<WeightedStyleRule> styleRules = new TreeSet<>(WeightedStyleRule::compare);
  private final MutableElement element;

  private ActiveStyles activeStyles = null;
  private WeightedStyleRule internalStyleRule = null;

  public ElementContextImp(MutableElement element) {
    this.element = element;
    updateStyle(element.attributes().get("style"));
  }
  
  @Override
  public void onCSSRuleMatched(WeightedStyleRule styleRule) {
    styleRules.add(styleRule);
    this.activeStyles = null;
  }

  @Override
  public void onCSSRuleUnmatched(WeightedStyleRule styleRule) {
    styleRules.remove(styleRule);
    this.activeStyles = null;
  }

  // TODO: Use a qualified name instead
  @Override
  public void onAttributeValueChanged(String attrName, String oldValue, String newValue) {
    if (attrName.equals("style")) {
      updateStyle(newValue);
    }
  }

  @Override
  public ActiveStyles activeStyles() {
    if (this.activeStyles == null) {
      ActiveStyles parentStyles = element.parentNode() instanceof MutableElement element ?
        ((ElementContext) element.getContext()).activeStyles() :
        null;
      this.activeStyles = ActiveStylesGenerator.generateActiveStyles(styleRules, parentStyles);
    }

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
      List<Declaration> declarations = CommonUtils.rethrow(() -> CSSParser.create().parseAStyleBlocksContents(tokenizerStream));
      // Need to do some dumb constructors to convert it to a WeightedStyleRule, maybe improve this later...
      StyleRule styleRule = StyleRule.create(List.of(), declarations);
      // also why wasn't there a .create anyways?
      WeightedStyleRule weightedStyleRule = new WeightedStyleRule(styleRule, ATTR_SPECIFICITY, RuleSource.AUTHOR, 0, 0);
      onCSSRuleMatched(weightedStyleRule);
    }
  }
  
}
