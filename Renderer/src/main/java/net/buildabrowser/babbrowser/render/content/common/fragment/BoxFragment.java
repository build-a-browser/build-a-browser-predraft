package net.buildabrowser.babbrowser.render.content.common.fragment;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;

public class BoxFragment extends LayoutFragment {
  
  private final ElementBox box;
  private final BoxPainter painter;

  public BoxFragment(
    float width, float height,
    ElementBox box, BoxPainter painter
  ) {
    super(width, height);
    this.box = box;
    this.painter = painter;
    box.updateFragment(this);
  }

  public ElementBox box() {
    return this.box;
  }

  public BoxPainter painter() {
    return this.painter;
  }

  @Override
  public float contentX() {
    float[] border = box.dimensions().getComputedBorder();
    float[] padding = box.dimensions().getComputedPadding();
    return borderX() + border[2] + padding[2];
  }

  @Override
  public float contentY() {
    float[] border = box.dimensions().getComputedBorder();
    float[] padding = box.dimensions().getComputedPadding();
    return borderY() + border[0] + padding[0];
  }

  @Override
  public float borderWidth() {
    float[] border = box.dimensions().getComputedBorder();
    float[] padding = box.dimensions().getComputedPadding();
    return contentWidth() + border[2] + border[3] + padding[2] + padding[3];
  }

  @Override
  public float borderHeight() {
    float[] border = box.dimensions().getComputedBorder();
    float[] padding = box.dimensions().getComputedPadding();
    return contentHeight() + border[0] + border[1] + padding[0] + padding[1];
  }

  // Margin utilities, particularly helpful for out-of-flow elements
  // WARNING: Does not account for margin collapsing

  @Override
  public float marginX() {
    float[] margin = box.dimensions().getComputedMargin();
    return borderX() - margin[2];
  }

  @Override
  public float marginY() {
    float[] margin = box.dimensions().getComputedMargin();
    return borderY() - margin[0];
  }

  @Override
  public float marginWidth() {
    float[] margin = box.dimensions().getComputedMargin();
    return borderWidth() + margin[2] + margin[3];
  }

  @Override
  public float marginHeight() {
    float[] margin = box.dimensions().getComputedMargin();
    return borderHeight() + margin[0] + margin[1];
  }

}
