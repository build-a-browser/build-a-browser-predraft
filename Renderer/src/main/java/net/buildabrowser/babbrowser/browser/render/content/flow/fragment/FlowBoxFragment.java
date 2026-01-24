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

  // Margin utilities, particularly helpful for out-of-flow elements
  // WARNING: Does not account for margin collapsing

  @Override
  public int marginX() {
    int[] margin = box.dimensions().getComputedMargin();
    return borderX() - margin[2];
  }

  @Override
  public int marginY() {
    int[] margin = box.dimensions().getComputedMargin();
    return borderY() - margin[0];
  }

  @Override
  public int marginWidth() {
    int[] margin = box.dimensions().getComputedMargin();
    return borderWidth() + margin[2] + margin[3];
  }

  @Override
  public int marginHeight() {
    int[] margin = box.dimensions().getComputedMargin();
    return borderHeight() + margin[0] + margin[1];
  }

}
