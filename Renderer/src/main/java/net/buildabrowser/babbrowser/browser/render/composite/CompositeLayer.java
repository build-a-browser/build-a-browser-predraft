package net.buildabrowser.babbrowser.browser.render.composite;

import net.buildabrowser.babbrowser.browser.render.composite.imp.CompositeLayerImp;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

// Despite the name, CompositeLayer does not support compositing at this time
public interface CompositeLayer {

  CompositeLayer createChild(BoxFragment childBoxFragment);

  void addChildLayer(CompositeLayer layer);

  PositionValue positioning();

  void paint(PaintCanvas canvas);

  void incOffset(float xOffset, float yOffset);

  float posX();

  float posY();

  float width();

  float height();

  int zIndex();

  static CompositeLayer createRoot(BoxFragment rootFragment) {
    return new CompositeLayerImp(null, rootFragment);
  }

}
