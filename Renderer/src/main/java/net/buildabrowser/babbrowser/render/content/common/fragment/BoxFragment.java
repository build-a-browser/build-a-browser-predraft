package net.buildabrowser.babbrowser.render.content.common.fragment;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

public class BoxFragment extends LayoutFragment {
  
  private final ElementBox box;
  private final BoxPainter painter;

  private final float inkWidth;
  private final float inkHeight;

  private float layerX = Float.NaN;
  private float layerY = Float.NaN;

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
    this.painter = UnreachableBoxPainter.create(box.element());
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
  public float posX(Measurement type) {
    return adjustCoord(super.posX(type), 2, type);
  }

  @Override
  public float posY(Measurement type) {
    return adjustCoord(super.posY(type), 0, type);
  }

  @Override
  public float width(Measurement type) {
    return adjustSize(super.width(Measurement.CONTENT), 2, type);
  }

  @Override
  public float inkWidth(Measurement type) {
    return adjustInk(width(type), inkWidth, 2, type);
  }

  @Override
  public float height(Measurement type) {
    return adjustSize(super.height(Measurement.CONTENT), 0, type);
  }

  @Override
  public float inkHeight(Measurement type) {
    return adjustInk(height(type), inkHeight, 0, type);
  }

  public float layerX(Measurement type) {
    assert !Float.isNaN(this.layerX);
    return adjustCoord(this.layerX, 2, type);
  }

  public float layerY(Measurement type) {
    assert !Float.isNaN(this.layerY);
    return adjustCoord(this.layerY, 0, type);
  }

  public void setLayerPos(float docX, float docY) {
    this.layerX = docX;
    this.layerY = docY;
  }
  
  public EventHandler eventHandler() {
    return box().content().eventHandler();
  }

  private float adjustSize(float size, int i, Measurement type) {
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

  private float adjustCoord(float pos, int i, Measurement type) {
    if (type.ordinal() <= Measurement.MARGIN.ordinal()) {
      float[] margin = box.dimensions().getComputedMargin();
      pos -= margin[i];
    }
    if (type.ordinal() > Measurement.BORDER.ordinal()) {
      float[] border = box.dimensions().getComputedBorder();
      pos += border[i];
    }
    if (type.ordinal() > Measurement.PADDING.ordinal()) {
      float[] padding = box.dimensions().getComputedPadding();
      pos += padding[i];
    }

    return pos;
  }

}
