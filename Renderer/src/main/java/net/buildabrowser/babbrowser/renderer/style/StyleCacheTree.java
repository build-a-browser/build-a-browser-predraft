package net.buildabrowser.babbrowser.renderer.style;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;

public interface StyleCacheTree {
  
  StyleCacheTree get(WeightedStyleRule rule);

  ActiveStyles get();

  void put(ActiveStyles activeStyles);

}
