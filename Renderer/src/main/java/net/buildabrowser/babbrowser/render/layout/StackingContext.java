package net.buildabrowser.babbrowser.render.layout;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.layout.imp.StackingContextImp;

public interface StackingContext {

  StackingContext createChild(ElementBox relatedBox);

  float[] computeInsets();

  void addFragment(float posX, float posY, BoxFragment fragment);

  CompositeLayer createLayer();

  void addLayer(Consumer<CompositeLayer> addFunc, float offsetX, float offsetY);

  StackingContext parentContext();

  PositionValue positioning();

  float innerWidth();

  float innerHeight();

  static StackingContext createRoot(ElementBox relatedBox) {
    return new StackingContextImp(null, relatedBox);
  }
  
  static boolean startsStackingContext(LayoutFragment fragment, BoxFragment refFragment) {
    if (!(fragment instanceof BoxFragment boxFragment)) return false;
    return boxFragment.box().stackingContext() != refFragment.box().stackingContext();
  }

}