package net.buildabrowser.babbrowser.render.layout;

import java.net.URI;

import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;

public record GlobalLayoutContext(
  URI refURL,
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  FontCache fontCache,
  ScriptingContext scriptingContext,
  Object cacheKey
) {
  
}
