package net.buildabrowser.babbrowser.browser.render.layout;

import java.net.URI;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.ResourceLoader;

public record GlobalLayoutContext(
  URI refURL,
  ResourceLoader resourceLoader,
  FontMetrics rootMetrics,
  Object cacheKey
) {
  
}
