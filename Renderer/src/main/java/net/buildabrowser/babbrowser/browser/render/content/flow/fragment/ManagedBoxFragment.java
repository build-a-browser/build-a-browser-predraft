package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class ManagedBoxFragment extends FlowFragment {

  private final ElementBox box;
  private final List<FlowFragment> fragments;

  public ManagedBoxFragment(
    int width, int height,
    ElementBox box, List<FlowFragment> fragments
  ) {
    super(width, height);
    this.box = box;
    this.fragments = fragments;
  }

  public ElementBox box() {
    return this.box;
  }

  public List<FlowFragment> fragments() {
    return this.fragments;
  }

}
