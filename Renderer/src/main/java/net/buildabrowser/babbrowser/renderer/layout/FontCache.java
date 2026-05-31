package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.layout.imp.FontCacheImp;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader.FontOptions;

public interface FontCache {

  LoadedFont load(FontOptions fontOptions);

  static FontCache create(FontLoader fontLoader) {
    return new FontCacheImp(fontLoader);
  }

}
