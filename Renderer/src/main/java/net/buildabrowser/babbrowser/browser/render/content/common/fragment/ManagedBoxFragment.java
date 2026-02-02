package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;

public class ManagedBoxFragment extends BoxFragment {

  private final List<LayoutFragment> fragments;

  public ManagedBoxFragment(
    int width, int height, ElementBox box,
    BoxPainter painter,
    List<LayoutFragment> fragments
  ) {
    super(width, height, box, painter);
    this.fragments = fragments;

    for (LayoutFragment fragment: fragments) {
      fragment.setParent(this);
    }
  }

  public ManagedBoxFragment(
    int x, int y, int width, int height, ElementBox box,
    List<LayoutFragment> fragments
  ) {
    this(width, height, box, null, fragments);
    setPos(x, y);
  }

  public List<LayoutFragment> fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[ManagedBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]");
    for (LayoutFragment fragment : fragments()) {
      textBuilder.append("\n\t" + fragment.toString().replace("\n", "\n\t"));
    }
    return textBuilder.toString();
  }

}
