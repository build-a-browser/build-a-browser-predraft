package net.buildabrowser.babbrowser.renderer.style.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

public class StyleCacheImp implements StyleCache {

  private final Map<SetKey, ActiveStyles> cacheMap = new HashMap<>();

  @Override
  public ActiveStyles lookupOrElse(
    Set<WeightedStyleRule> relatedRules,
    Function<Set<WeightedStyleRule>, ActiveStyles> stylesGenerator
  ) {
    int setHash = relatedRules.hashCode();
    SetKey setKey = new SetKey(setHash, relatedRules);
    ActiveStyles existingStyles = cacheMap.get(setKey);
    if (existingStyles != null) return existingStyles;
    ActiveStyles newStyles = stylesGenerator.apply(relatedRules);
    if (newStyles.isReusable()) {
      cacheMap.put(setKey, newStyles);
    }
    return newStyles;
  }

  private record SetKey(
    int setHash,
    Set<WeightedStyleRule> set
  ) {

    @Override
    public int hashCode() {
      return this.setHash;
    }

  }
  
}
