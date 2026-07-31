package net.buildabrowser.babbrowser.dom.events;

import net.buildabrowser.babbrowser.dom.events.imp.PointerEventImp;

public interface PointerEvent extends MouseEvent {
  
  float layerX();

  float layerY();

  static PointerEvent createGeneric(String type) {
    return new PointerEventImp(type, (byte) 0, 0, 0);
  }

  static PointerEvent create(
    String type, byte modifiers,
    float layerX, float layerY
  ) {
    return new PointerEventImp(type, modifiers, layerX, layerY);
  }

}
