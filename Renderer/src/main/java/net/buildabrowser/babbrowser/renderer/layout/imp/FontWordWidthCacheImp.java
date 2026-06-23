package net.buildabrowser.babbrowser.renderer.layout.imp;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.layout.FontWordWidthCache;

public class FontWordWidthCacheImp implements FontWordWidthCache {

  private static int MAX_CACHE_SIZE = 10000;

  // LoadedFont is not slottable, and I don't want to make it so
  private Map<FontMetrics, HashMap<String, Float>> wordWidths = new WeakHashMap<>();

  @Override 
  public float stringWidth(FontMetrics fontMetrics, String word) {
    Map<String, Float> fontWordWidths = wordWidths.computeIfAbsent(
      // TODO: LinkedHashMap uses more memory, but I need it for removeEldestEntry
      fontMetrics, _1 -> new LinkedHashMap<>(128, .75f, true) {
        @Override
        protected boolean removeEldestEntry(
          Map.Entry<String, Float> eldest
        ) {
          return size() > MAX_CACHE_SIZE;
        }
      });
    
    return fontWordWidths.computeIfAbsent(
      word, _1 -> fontMetrics.stringWidth(word));
  }
  
}
