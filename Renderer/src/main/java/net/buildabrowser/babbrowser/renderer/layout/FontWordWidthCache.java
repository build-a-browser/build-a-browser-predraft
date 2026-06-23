package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.layout.imp.FontWordWidthCacheImp;

public interface FontWordWidthCache {

  float stringWidth(FontMetrics fontMetrics, String word);

  static FontWordWidthCache create() {
    return new FontWordWidthCacheImp();
  }
  
}
