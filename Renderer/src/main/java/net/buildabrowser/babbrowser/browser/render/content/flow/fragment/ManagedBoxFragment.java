package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class ManagedBoxFragment extends FlowBoxFragment {

  private final List<FlowFragment> fragments;

  public ManagedBoxFragment(
    int width, int height,
    ElementBox box, List<FlowFragment> fragments
  ) {
    super(width, height, box);
    this.fragments = fragments;
  }

  public ManagedBoxFragment(
    int x, int y, int width, int height,
    ElementBox box, List<FlowFragment> fragments
  ) {
    this(width, height, box, fragments);
    setPos(x, y);
  }

  public List<FlowFragment> fragments() {
    return this.fragments;
  }

  @Override
  public String toString() {
    StringBuilder textBuilder = new StringBuilder();
    textBuilder.append("[ManagedBoxFragment pos=[" + borderX() + ", " + borderY() + "] size=[" + contentWidth() + "x" + contentHeight() + "]]");
    for (FlowFragment fragment : fragments()) {
      textBuilder.append("\n\t" + fragment.toString().replace("\n", "\n\t"));
    }
    return textBuilder.toString();
  }

}
