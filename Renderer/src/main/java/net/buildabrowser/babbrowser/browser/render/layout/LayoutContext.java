package net.buildabrowser.babbrowser.browser.render.layout;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;

public record LayoutContext(
  URL refURL,
  FontMetrics fontMetrics
) {
  
}
