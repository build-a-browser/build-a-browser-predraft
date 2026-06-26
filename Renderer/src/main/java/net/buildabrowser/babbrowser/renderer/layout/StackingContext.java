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

  void positionFragment(
    float posX, float posY,
    BoxFragment<?> fragment,
    ChildPositionFunc positionFunc
  );

  // TODO: I don't really like this method, especially since it
  // can be called before the normalized bounds are determined
  void positionNormalizedFragment(
    float posX, float posY,
    BoxFragment<?> fragment,
    ChildPositionFunc positionFunc
  );

  CompositeLayer createLayer(Painter painter);

  void addLayer(
    Consumer<CompositeLayer> addFunc,
    Painter painter,
    StackingContextPosition parentPosition
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

  static interface ChildPositionFunc {
  
    void position(float layerX, float layerY);
    
  }

}