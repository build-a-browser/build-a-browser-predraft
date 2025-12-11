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

  public List<FlowFragment> fragments() {
    return this.fragments;
  }

}
