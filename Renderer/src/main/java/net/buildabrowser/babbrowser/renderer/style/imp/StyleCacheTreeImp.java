package net.buildabrowser.babbrowser.renderer.style.imp;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.renderer.style.StyleCacheTree;

public class StyleCacheTreeImp implements StyleCacheTree {

  private WeakHashMap<WeightedStyleRule, StyleCacheTree> subtrees;

  private WeakReference<ActiveStyles> activeStyles;

  @Override
  public StyleCacheTree get(WeightedStyleRule rule) {
    if (subtrees == null) {
      subtrees = new WeakHashMap<>(4);
    }

    return subtrees.computeIfAbsent(rule, _1 -> new StyleCacheTreeImp());
  }

  @Override
  public ActiveStyles get() {
    if (this.activeStyles == null) return null;
    return this.activeStyles.get();
  }

  @Override
  public void put(ActiveStyles activeStyles) {
    this.activeStyles = new WeakReference<>(activeStyles);
  }
  
}
