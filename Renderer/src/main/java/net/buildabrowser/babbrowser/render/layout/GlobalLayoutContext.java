package net.buildabrowser.babbrowser.render.layout;

import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.image.ImageCache;
import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.ResourceLoader;

public record GlobalLayoutContext(
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  FontCache fontCache,
  Viewport viewport,
  ScriptingContext scriptingContext,
  ImageCache imageCache
) {
  
}
