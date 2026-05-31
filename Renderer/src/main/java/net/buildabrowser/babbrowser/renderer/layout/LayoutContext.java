package net.buildabrowser.babbrowser.renderer.layout;

import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedFont;

public record LayoutContext(
  GlobalLayoutContext global,
  LoadedFont font
) {

}
