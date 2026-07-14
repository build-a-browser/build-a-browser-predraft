package net.buildabrowser.babbrowser.dom.events;

public interface PointerEvent extends MouseEvent {
  
  float layerX();

  float layerY();
  
  static record BasePointerEvent(
    String type, float layerX, float layerY
  ) implements PointerEvent {}

  static PointerEvent createGeneric(String type) {
    return new BasePointerEvent(type, 0, 0);
  }

  static PointerEvent create(
    String type, float layerX, float layerY
  ) {
    return new BasePointerEvent(type, layerX, layerY);
  }

}
