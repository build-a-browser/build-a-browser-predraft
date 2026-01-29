package net.buildabrowser.babbrowser.browser.render.composite;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.composite.imp.CompositeLayerImp;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

// Despite the name, CompositeLayer does not support compositing at this time
public interface CompositeLayer {

  List<CompositeLayer> childLayers();

  CompositeLayer createChild(BoxFragment childBoxFragment);

  void addChildLayer(CompositeLayer layer);

  PositionValue positioning();

  boolean isPassthrough();

  void paint(PaintCanvas canvas);

  void incOffset(int xOffset, int yOffset);

  int posX();

  int posY();

  int width();

  int height();

  int zIndex();

  static CompositeLayer createRoot(BoxFragment rootFragment) {
    return new CompositeLayerImp(null, rootFragment);
  }

}
