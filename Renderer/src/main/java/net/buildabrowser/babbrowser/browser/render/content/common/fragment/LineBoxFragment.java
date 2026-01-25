package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import java.util.List;

public class LineBoxFragment extends LayoutFragment {

  private final List<LayoutFragment> fragments;

  public LineBoxFragment(
    int width, int height, List<LayoutFragment> fragments
  ) {
    super(width, height);
    this.fragments = fragments;
  }

  public LineBoxFragment(
    int x, int y, int width, int height, List<LayoutFragment> fragments
  ) {
    this(width, height, fragments);
    setPos(x, y);
  }

  public List<LayoutFragment> fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[LineBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]");
    for (LayoutFragment fragment : fragments()) {
      textBuilder.append("\n\t" + fragment.toString().replace("\n", "\n\t"));
    }

    return textBuilder.toString();
  }

}
