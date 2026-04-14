package net.buildabrowser.babbrowser.render.layout;

import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;

public record GlobalLayoutContext(
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  FontCache fontCache,
  ScriptingContext scriptingContext,
  Object cacheKey
) {
  
}
