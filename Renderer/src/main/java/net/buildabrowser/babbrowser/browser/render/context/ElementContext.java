package net.buildabrowser.babbrowser.browser.render.context;

import net.buildabrowser.babbrowser.browser.render.context.imp.ElementContextImp;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;

public interface ElementContext {
  
  void onCSSRuleMatched(StyleRule styleRule);

  void onCSSRuleUnmatched(StyleRule styleRule);

  ActiveStyles activeStyles();

  static ElementContext create() {
    return new ElementContextImp();
  }

}
