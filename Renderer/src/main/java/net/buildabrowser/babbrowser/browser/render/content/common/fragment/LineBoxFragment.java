package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import java.util.List;

public class LineBoxFragment extends LayoutFragment {

  private final List<LayoutFragment> fragments;

  private LayoutFragment parentFragment;

  public LineBoxFragment(
    int width, int height, List<LayoutFragment> fragments
  ) {
    super(width, height);
    this.fragments = fragments;

    for (LayoutFragment fragment: fragments) {
      fragment.setParent(this);
    }
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

  public void setParent(LayoutFragment parent) {
    this.parentFragment = parent;
  }

  @Override
  public int layerX() {
    assert parentFragment != null;
    return parentFragment.layerX() + contentX();
  }

  @Override
  public int layerY() {
    assert parentFragment != null;
    return parentFragment.layerY() + contentY();
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
