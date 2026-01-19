package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;

public record LineSegment(
  ElementBox box,
  List<FlowFragment> fragments
) {

  public int width() {
    int width = 0;
    for (FlowFragment fragment: fragments) {
      width += fragment.contentWidth();
    }

    return width;
  }

  public int height() {
    int height = 0;
    for (FlowFragment fragment: fragments) {
      height = Math.max(height, fragment.contentHeight());
    }

    return height;
  }

}