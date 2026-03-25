package net.buildabrowser.babbrowser.browser.render.layout;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.ResourceLoader;
import net.buildabrowser.babbrowser.fetch.FetchEngine;

public record GlobalLayoutContext(
  URI refURL,
  ResourceLoader resourceLoader,
  FetchEngine fetchEngine,
  FontMetrics rootMetrics,
  FontCache fontCache,
  Object cacheKey
) {
  
}
