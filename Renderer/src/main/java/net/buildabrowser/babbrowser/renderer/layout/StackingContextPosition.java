package net.buildabrowser.babbrowser.renderer.layout;

// TODO: Maybe see about backing this with a matrix, might help
// when adding rotations and stuff
public record StackingContextPosition(
  float docX, float docY,
  ScrollGetter scrollX, ScrollGetter scrollY
) {

  public int vpX() {
    return (int) (docX - scrollX.get());
  }

  public int vpY() {
    return (int) (docY - scrollY.get());
  }

  public StackingContextPosition absolute(
    float offsetX, float offsetY
  ) {
    return absolute(this, offsetX, offsetY);
  }

  public StackingContextPosition relative(
    float offsetX, float offsetY,
    float itemX, float itemY
  ) {
    return relative(this, offsetX, offsetY, itemX, itemY);
  }

  public StackingContextPosition scroll(
    ScrollGetter scrollGetterX,
    ScrollGetter scrollGetterY
  ) {
    return scroll(this, scrollGetterX, scrollGetterY);
  }

  public static StackingContextPosition absolute(
    StackingContextPosition parent,
    float offsetX, float offsetY
  ) {
    return new StackingContextPosition(
      parent.docX() + offsetX,
      parent.docY() + offsetY,
      parent.scrollX(),
      parent.scrollY());
  }

  public static StackingContextPosition relative(
    StackingContextPosition parent,
    float offsetX, float offsetY,
    float itemX, float itemY
  ) {
    return new StackingContextPosition(
      parent.docX() + offsetX + itemX,
      parent.docY() + offsetY + itemY,
      parent.scrollX(),
      parent.scrollY());
  }

  public static StackingContextPosition scroll(
    StackingContextPosition parent,
    ScrollGetter scrollGetterX,
    ScrollGetter scrollGetterY
  ) {
    return new StackingContextPosition(
      parent.docX(), parent.docY(),
      () -> parent.scrollX().get() + scrollGetterX.get(),
      () -> parent.scrollY().get() + scrollGetterY.get());
  }

  public static StackingContextPosition root() {
    return new StackingContextPosition(0, 0, () -> 0, () -> 0);
  }
  
  public static interface ScrollGetter {
  
    float get();
    
  }

}
