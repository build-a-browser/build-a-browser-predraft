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
    int[] border = box.dimensions().getComputedBorder();
    int[] padding = box.dimensions().getComputedPadding();
    return borderX() + border[2] + padding[2];
  }

  @Override
  public int contentY() {
    int[] border = box.dimensions().getComputedBorder();
    int[] padding = box.dimensions().getComputedPadding();
    return borderY() + border[0] + padding[0];
  }

  @Override
  public int borderWidth() {
    int[] border = box.dimensions().getComputedBorder();
    int[] padding = box.dimensions().getComputedPadding();
    return contentWidth() + border[2] + border[3] + padding[2] + padding[3];
  }

  @Override
  public int borderHeight() {
    int[] border = box.dimensions().getComputedBorder();
    int[] padding = box.dimensions().getComputedPadding();
    return contentHeight() + border[0] + border[1] + padding[0] + padding[1];
  }

}
