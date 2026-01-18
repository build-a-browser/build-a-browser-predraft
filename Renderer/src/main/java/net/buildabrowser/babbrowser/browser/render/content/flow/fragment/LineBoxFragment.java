package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

import java.util.List;

public class LineBoxFragment extends FlowFragment {

  private final List<FlowFragment> fragments;

  public LineBoxFragment(
    int width, int height, List<FlowFragment> fragments
  ) {
    super(width, height);
    this.fragments = fragments;
  }

  public LineBoxFragment(
    int x, int y, int width, int height, List<FlowFragment> fragments
  ) {
    this(width, height, fragments);
    setPos(x, y);
  }

  public List<FlowFragment> fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[LineBoxFragment pos=[" + posX() + ", " + posY() + "] size=[" + width() + "x" + height() + "]]");
    for (FlowFragment fragment : fragments()) {
      textBuilder.append("\n\t" + fragment.toString().replace("\n", "\n\t"));
    }

    return textBuilder.toString();
  }

}
