package net.buildabrowser.babbrowser.browser.render.layout;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.ResourceLoader;

public record GlobalLayoutContext(
  URI refURL,
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  FontCache fontCache,
  ScriptingContext scriptingContext,
  Object cacheKey
) {
  
}
