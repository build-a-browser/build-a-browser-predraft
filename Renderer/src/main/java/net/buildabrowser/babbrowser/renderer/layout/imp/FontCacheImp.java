package net.buildabrowser.babbrowser.renderer.layout.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.renderer.layout.FontCache;

public class FontCacheImp implements FontCache {
  
  private final FontLoader fontLoader;

  private final Map<FontOptions, LoadedFont> fontCache = new HashMap<>();

  public FontCacheImp(FontLoader fontLoader) {
    this.fontLoader = fontLoader;
  }

  public LoadedFont load(FontOptions fontOptions) {
    return fontCache.computeIfAbsent(fontOptions, _1 -> fontLoader.load(fontOptions));
  }

}
