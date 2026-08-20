package net.buildabrowser.babbrowser.renderer.style;

import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.style.imp.StyleCacheImp;

public interface StyleCache {
  
  ActiveStyles lookupOrElse(
    List<WeightedStyleRule> relatedRules,
    Function<List<WeightedStyleRule>, ActiveStyles> stylesGenerator
  );

  PropertyContainer cacheFlattened(PropertyContainer flattenedStyles);

  static StyleCache create() {
    return new StyleCacheImp();
  }

}
