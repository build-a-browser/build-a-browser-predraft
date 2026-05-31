package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.renderer.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.renderer.paint.backend.ResourceLoader;

public record GlobalLayoutContext(
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  FontCache fontCache,
  Viewport viewport,
  ScriptingContext scriptingContext,
  ImageCache imageCache
) {
  
}
