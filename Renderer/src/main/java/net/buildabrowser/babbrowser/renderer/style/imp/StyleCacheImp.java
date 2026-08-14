package net.buildabrowser.babbrowser.renderer.style.imp;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;
import net.buildabrowser.babbrowser.renderer.style.StyleCacheTree;

public class StyleCacheImp implements StyleCache {

  // StyleCache is short lived, so don't bother with weak maps
  private final StyleCacheTree cacheTree = new StyleCacheTreeImp();
  private final WeakHashMap<PropertyContainer, WeakReference<PropertyContainer>> cacheMap2 = new WeakHashMap<>();

  @Override
  public ActiveStyles lookupOrElse(
    List<WeightedStyleRule> relatedRules,
    Function<List<WeightedStyleRule>, ActiveStyles> stylesGenerator
  ) {
    StyleCacheTree targetTree = cacheTree;
    for (WeightedStyleRule rule: relatedRules) {
      targetTree = targetTree.get(rule);
    }

    ActiveStyles existingStyles = targetTree.get();
    if (existingStyles != null) return existingStyles;

    ActiveStyles newStyles = stylesGenerator.apply(relatedRules);
    if (newStyles.isReusable()) {
      targetTree.put(newStyles);
    }
    return newStyles;
  }

  @Override
  public PropertyContainer cacheFlattened(PropertyContainer flattenedStyles) {
    if (!flattenedStyles.isReusable()) return flattenedStyles;
    PropertyContainer styleRef = cacheMap2.computeIfAbsent(
      flattenedStyles, _1 -> new WeakReference<>(flattenedStyles)).get();
    if (styleRef != null) return styleRef;
    cacheMap2.put(styleRef, new WeakReference<>(styleRef));
    return styleRef;
  }
  
}
