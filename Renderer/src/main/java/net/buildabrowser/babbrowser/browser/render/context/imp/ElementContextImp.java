package net.buildabrowser.babbrowser.browser.render.context.imp;

import java.util.HashSet;
import java.util.Set;

import net.buildabrowser.babbrowser.browser.render.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesGenerator;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;

public class ElementContextImp implements ElementContext {

  private final Set<StyleRule> styleRules = new HashSet<>(2);

  private ActiveStyles activeStyles = null;
  
  @Override
  public void onCSSRuleMatched(StyleRule styleRule) {
    styleRules.add(styleRule);
    this.activeStyles = null;
  }

  @Override
  public void onCSSRuleUnmatched(StyleRule styleRule) {
    styleRules.remove(styleRule);
    this.activeStyles = null;
  }

  @Override
  public ActiveStyles activeStyles() {
    if (this.activeStyles == null) {
      this.activeStyles = ActiveStylesGenerator.generateActiveStyles(styleRules);
    }

    return this.activeStyles;
  }
  
}
