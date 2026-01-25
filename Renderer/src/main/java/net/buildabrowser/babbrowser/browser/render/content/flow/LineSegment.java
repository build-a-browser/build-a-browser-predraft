package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;

public record LineSegment(
  ElementBox box,
  List<LayoutFragment> fragments
) {

  public int width() {
    int width = 0;
    for (LayoutFragment fragment: fragments) {
      width += fragment.borderWidth();
    }

    return width;
  }

  public int height() {
    int height = 0;
    for (LayoutFragment fragment: fragments) {
      height = Math.max(height, fragment.borderHeight());
    }

    return height;
  }

}