package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.painter.core.LoadedFont;

public record LayoutContext(
  GlobalLayoutContext global,
  LoadedFont font
) {

}
