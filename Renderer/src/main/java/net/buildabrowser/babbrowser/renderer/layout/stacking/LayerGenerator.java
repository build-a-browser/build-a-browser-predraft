package net.buildabrowser.babbrowser.renderer.layout.stacking;

public interface LayerGenerator<T> {

  T createLayer(
    StackingContextPosition position,
    int zIndexOrder,
    StackingContextEntry entries
  );

  void addChild(
    T layer,
    T child
  );
  
}
