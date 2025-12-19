package net.buildabrowser.babbrowser.browser.render.layout;

import java.net.URL;

import net.buildabrowser.babbrowser.browser.network.URLUtil;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.dom.utils.CommonUtils;

public record LayoutContext(
  URL refURL,
  FontMetrics fontMetrics
) {
  
  public LayoutContext(FontMetrics fontMetrics) {
    this(CommonUtils.rethrow(() -> URLUtil.createURL("https://example.com")), fontMetrics);
  }

}
