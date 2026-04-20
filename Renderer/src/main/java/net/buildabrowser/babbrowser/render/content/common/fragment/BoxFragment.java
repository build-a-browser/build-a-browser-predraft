package net.buildabrowser.babbrowser.render.content.common.fragment;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

public class BoxFragment extends LayoutFragment {
  
  private final ElementBox box;
  private final BoxPainter painter;

  private final float inkWidth;
  private final float inkHeight;

  public BoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, BoxPainter painter
  ) {
    super(width, height);
    this.box = box;
    this.painter = painter;
    this.inkWidth = inkWidth;
    this.inkHeight = inkHeight;
  }

  public BoxFragment(
    float width, float height,
    ElementBox box
  ) {
    super(width, height);
    this.box = box;
    this.painter = new UnreachableBoxPainter();
    this.inkWidth = width;
    this.inkHeight = height;
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

  @Override
  public float width(Measurement type) {
    return adjustNormal(super.width(Measurement.CONTENT), 2, type);
  }

  @Override
  public float inkWidth(Measurement type) {
    return adjustInk(width(type), inkWidth, 2, type);
  }

  @Override
  public float height(Measurement type) {
    return adjustNormal(super.height(Measurement.CONTENT), 0, type);
  }

  @Override
  public float inkHeight(Measurement type) {
    return adjustInk(height(type), inkHeight, 0, type);
  }

  private float adjustNormal(float size, int i, Measurement type) {
    if (type.ordinal() <= Measurement.MARGIN.ordinal()) {
      float[] margin = box.dimensions().getComputedMargin();
      size += margin[i] + margin[i + 1];
    }
    if (type.ordinal() <= Measurement.BORDER.ordinal()) {
      float[] border = box.dimensions().getComputedBorder();
      size += border[i] + border[i + 1];
    }
    if (type.ordinal() <= Measurement.PADDING.ordinal()) {
      float[] padding = box.dimensions().getComputedPadding();
      size += padding[i] + padding[i + 1];
    }

    return size;
  }

  private float adjustInk(float normalSize, float inkSize, int i, Measurement type) {
    if (type.ordinal() <= Measurement.MARGIN.ordinal()) {
      float[] margin = box.dimensions().getComputedMargin();
      inkSize += margin[i];
    }
    if (type.ordinal() <= Measurement.BORDER.ordinal()) {
      float[] border = box.dimensions().getComputedBorder();
      inkSize += border[i];
    }
    if (type.ordinal() <= Measurement.PADDING.ordinal()) {
      float[] padding = box.dimensions().getComputedPadding();
      inkSize += padding[i];
    }

    return Math.max(normalSize, inkSize);
  }

}
