package net.buildabrowser.babbrowser.render.layout;

import net.buildabrowser.babbrowser.render.paint.LoadedFont;

public record LayoutContext(
  GlobalLayoutContext global,
  LoadedFont font
) {

}
