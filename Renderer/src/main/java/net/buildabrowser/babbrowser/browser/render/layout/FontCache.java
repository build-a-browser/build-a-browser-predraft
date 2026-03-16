package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.layout.imp.FontCacheImp;
import net.buildabrowser.babbrowser.browser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.browser.render.paint.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedFont;

public interface FontCache {

  LoadedFont load(FontOptions fontOptions);

  static FontCache create(FontLoader fontLoader) {
    return new FontCacheImp(fontLoader);
  }

}
