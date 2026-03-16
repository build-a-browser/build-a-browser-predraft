package net.buildabrowser.babbrowser.browser.render.layout.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.browser.render.layout.FontCache;
import net.buildabrowser.babbrowser.browser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.browser.render.paint.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedFont;

public class FontCacheImp implements FontCache {
  
  private final FontLoader fontLoader;

  private final Map<FontOptions, LoadedFont> fontCache = new HashMap<>();

  public FontCacheImp(FontLoader fontLoader) {
    this.fontLoader = fontLoader;
  }

  public LoadedFont load(FontOptions fontOptions) {
    return fontCache.computeIfAbsent(fontOptions, _ -> fontLoader.load(fontOptions));
  }

}
