package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;

public record LayoutContext(
  GlobalLayoutContext global,
  FontMetrics fontMetrics
) {

}
