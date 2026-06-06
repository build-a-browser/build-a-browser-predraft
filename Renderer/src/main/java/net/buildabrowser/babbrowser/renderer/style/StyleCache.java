package net.buildabrowser.babbrowser.renderer.style;

import java.util.Set;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.renderer.style.imp.StyleCacheImp;

public interface StyleCache {
  
  ActiveStyles lookupOrElse(
    Set<WeightedStyleRule> relatedRules,
    Function<Set<WeightedStyleRule>, ActiveStyles> stylesGenerator
  );

  static StyleCache create() {
    return new StyleCacheImp();
  }

}
