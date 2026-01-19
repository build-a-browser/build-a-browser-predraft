package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;

public class FlowBoxFragment extends FlowFragment {
  
  private final ElementBox box;

  public FlowBoxFragment(int width, int height, ElementBox box) {
    super(width, height);
    this.box = box;
  }

  public ElementBox box() {
    return this.box;
  }

  @Override
  public int contentX() {
    int[] padding = box.dimensions().getComputedPadding();
    return borderX() + padding[2];
  }

  @Override
  public int contentY() {
    int[] padding = box.dimensions().getComputedPadding();
    return borderY() + padding[0];
  }

  @Override
  public int borderWidth() {
    int[] padding = box.dimensions().getComputedPadding();
    return contentWidth() + padding[2] + padding[3];
  }

  @Override
  public int borderHeight() {
    int[] padding = box.dimensions().getComputedPadding();
    return contentHeight() + padding[0] + padding[1];
  }

}
