package net.buildabrowser.babbrowser.renderer.style.imp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public class StyleCacheImp implements StyleCache {

  private final Map<ListKey, ActiveStyles> cacheMap = new HashMap<>();

  @Override
  public ActiveStyles lookupOrElse(
    List<WeightedStyleRule> relatedRules,
    Function<List<WeightedStyleRule>, ActiveStyles> stylesGenerator
  ) {
    int listHash = relatedRules.hashCode();
    ListKey hashKey = new ListKey(listHash, relatedRules);
    ActiveStyles existingStyles = cacheMap.get(hashKey);
    if (existingStyles != null) return existingStyles;
    ActiveStyles newStyles = stylesGenerator.apply(relatedRules);
    if (newStyles.isReusable()) {
      cacheMap.put(
        new ListKey(listHash, List.copyOf(relatedRules)),
        newStyles);
    }
    return newStyles;
  }

  private record ListKey(
    int listHash,
    List<WeightedStyleRule> list
  ) {

    @Override
    public int hashCode() {
      return this.listHash;
    }

  }
  
}
