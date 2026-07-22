package net.buildabrowser.babbrowser.renderer.layout;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayer;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.layout.imp.StackingContextImp;

public interface StackingContext {

  StackingContext createChild(ElementBox relatedBox);

  float[] computeInsets();

  void setAbsolutePosition(float[] position); // TODO: Not the cleanest method

  float[] computedBorder(); // TODO: Also not very clean

  <T extends BoxFragment<?>> void positionFragment(
    float posX, float posY,
    T fragment,
    ChildPositionFunc<T> positionFunc
  );

  // TODO: I don't really like this method, especially since it
  // can be called before the normalized bounds are determined
  <T extends BoxFragment<?>> void positionNormalizedFragment(
    float posX, float posY,
    T fragment,
    ChildPositionFunc<T> positionFunc
  );

  CompositeLayer createLayer(Painter painter);

  void addLayer(
    Consumer<CompositeLayer> addFunc,
    Painter painter,
    StackingContextPosition parentPosition,
    ScrollPort scrollPort
  );

  StackingContext parentContext();

  PositionValue positioning();

  float innerWidth();

  float innerHeight();

  static StackingContext createRoot(ElementBox relatedBox) {
    return new StackingContextImp(null, relatedBox);
  }
  
  static boolean startsStackingContext(LayoutFragment fragment, BoxFragment<?> refFragment) {
    if (!(fragment instanceof BoxFragment boxFragment)) return false;
    return boxFragment.box().stackingContext() != refFragment.box().stackingContext();
  }

  static interface ChildPositionFunc<T extends BoxFragment<?>> {
  
    void position(
      T fragment,
      float layerX, float layerY
    );
    
  }

}