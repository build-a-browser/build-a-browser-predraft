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

  public StackingContextPosition relative(
    float offsetX, float offsetY,
    float itemX, float itemY
  ) {
    return relative(this, offsetX, offsetY, itemX, itemY);
  }

  public StackingContextPosition sticky(
    float itemX, float itemY,
    float itemWidth, float itemHeight,
    float[] insets, ScrollPort scrollPort
  ) {
    return sticky(
      this, itemX, itemY, itemWidth, itemHeight,
      insets, scrollPort);
  }

  public StackingContextPosition absolute(
    float offsetX, float offsetY
  ) {
    return absolute(this, offsetX, offsetY);
  }

  public StackingContextPosition fixed(
    float offsetX, float offsetY,
    boolean isStaticX, boolean isStaticY
  ) {
    return fixed(this, offsetX, offsetY, isStaticX, isStaticY);
  }

  public StackingContextPosition scroll(
    ScrollGetter scrollGetterX,
    ScrollGetter scrollGetterY
  ) {
    return scroll(this, scrollGetterX, scrollGetterY);
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

  public static StackingContextPosition sticky(
    StackingContextPosition parent,
    float itemX, float itemY,
    float itemWidth, float itemHeight,
    float[] insets, ScrollPort scrollPort
  ) {
    ScrollGetter xGetter = () -> {
      float rawItemVpX = parent.vpX() + itemX;
      float itemVpX = rawItemVpX;
      float scrollPortStartX = scrollPort.position().vpX() + insets[2];
      float scrollPortEndX = scrollPort.position().vpX() + scrollPort.width() - insets[3];
      if (!Float.isNaN(insets[3]) && itemVpX + itemWidth > scrollPortEndX) {
        itemVpX = scrollPortEndX - itemWidth;
      }
      if (!Float.isNaN(insets[2]) && itemVpX < scrollPortStartX) {
        itemVpX = scrollPortStartX;
      }
      
      return parent.scrollX().get() + -(itemVpX - rawItemVpX);
    };

    ScrollGetter yGetter = () -> {
      float rawItemVpY = parent.vpY() + itemY;
      float itemVpY = rawItemVpY;
      float scrollPortStartY = scrollPort.position().vpY() + insets[0];
      float scrollPortEndY = scrollPort.position().vpY() + scrollPort.height() - insets[1];
      if (!Float.isNaN(insets[1]) && itemVpY + itemHeight > scrollPortEndY) {
        itemVpY = scrollPortEndY - itemHeight;
      }
      if (!Float.isNaN(insets[0]) && itemVpY < scrollPortStartY) {
        itemVpY = scrollPortStartY;
      }
      
      return parent.scrollY().get() + -(itemVpY - rawItemVpY);
    };

    return new StackingContextPosition(
      parent.docX() + itemX,
      parent.docY() + itemY,
      xGetter, yGetter);
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

  public static StackingContextPosition fixed(
    StackingContextPosition parent,
    float offsetX, float offsetY,
    boolean isStaticX, boolean isStaticY
  ) {
    return new StackingContextPosition(
      offsetX + (isStaticX ? parent.docX() : 0),
      offsetY + (isStaticY ? parent.docY() : 0),
      () -> 0, () -> 0);
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
