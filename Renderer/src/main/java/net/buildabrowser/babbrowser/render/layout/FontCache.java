package net.buildabrowser.babbrowser.render.layout;

import net.buildabrowser.babbrowser.render.layout.imp.FontCacheImp;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader.FontOptions;

public interface FontCache {

  LoadedFont load(FontOptions fontOptions);

  static FontCache create(FontLoader fontLoader) {
    return new FontCacheImp(fontLoader);
  }

}
