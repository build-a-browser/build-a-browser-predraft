package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.renderer.layout.imp.FontCacheImp;

public interface FontCache {

  LoadedFont load(FontOptions fontOptions);

  static FontCache create(FontLoader fontLoader) {
    return new FontCacheImp(fontLoader);
  }

}
