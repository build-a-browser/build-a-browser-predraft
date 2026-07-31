package net.buildabrowser.babbrowser.dom.events.imp;

import net.buildabrowser.babbrowser.dom.events.PointerEvent;

public class PointerEventImp extends MouseEventImp implements PointerEvent {

  private final float layerX;
  private final float layerY;

  public PointerEventImp(
    String type, byte modifiers,
    float layerX, float layerY
  ) {
    super(type, modifiers);
    this.layerX = layerX;
    this.layerY = layerY;
  }

  @Override
  public float layerX() {
    return this.layerX;
  }

  @Override
  public float layerY() {
    return this.layerY;
  }
  
}
