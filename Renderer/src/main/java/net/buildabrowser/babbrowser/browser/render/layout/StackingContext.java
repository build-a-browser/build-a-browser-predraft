package net.buildabrowser.babbrowser.browser.render.layout;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.layout.imp.StackingContextImp;

public interface StackingContext {

  StackingContext createChild(ElementBox relatedBox);

  float[] computeInsets(LayoutContext layoutContext);

  void addFragment(float posX, float posY, BoxFragment fragment);

  CompositeLayer createLayer();

  void addLayer(Consumer<CompositeLayer> addFunc, float offsetX, float offsetY);

  StackingContext parentContext();

  float posX();

  float posY();

  float computeWidth();

  float computeHeight();

  static StackingContext createRoot(ElementBox relatedBox) {
    return new StackingContextImp(null, relatedBox);
  }
  
  static boolean startsStackingContext(LayoutFragment fragment, BoxFragment refFragment) {
    if (!(fragment instanceof BoxFragment boxFragment)) return false;
    return boxFragment.box().stackingContext() != refFragment.box().stackingContext();
  }

}