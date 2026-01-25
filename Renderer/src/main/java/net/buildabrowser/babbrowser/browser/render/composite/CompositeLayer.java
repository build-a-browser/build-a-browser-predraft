package net.buildabrowser.babbrowser.browser.render.composite;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.composite.imp.RootCompositeLayerImp;
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

  static CompositeLayer createRoot() {
    return new RootCompositeLayerImp();
  }

}
