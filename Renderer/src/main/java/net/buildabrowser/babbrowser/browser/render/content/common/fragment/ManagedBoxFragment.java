package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class ManagedBoxFragment extends BoxFragment {

  private final List<LayoutFragment> fragments;

  public ManagedBoxFragment(
    int width, int height,
    ElementBox box, List<LayoutFragment> fragments
  ) {
    super(width, height, box);
    this.fragments = fragments;
  }

  public ManagedBoxFragment(
    int x, int y, int width, int height,
    ElementBox box, List<LayoutFragment> fragments
  ) {
    this(width, height, box, fragments);
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
