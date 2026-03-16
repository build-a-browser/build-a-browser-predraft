package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.paint.LoadedFont;

public record LayoutContext(
  GlobalLayoutContext global,
  LoadedFont font
) {

}
