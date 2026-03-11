package net.buildabrowser.babbrowser.browser.render.composite;

import net.buildabrowser.babbrowser.browser.render.composite.imp.CompositeLayerImp;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;

// Despite the name, CompositeLayer does not support compositing at this time
public interface CompositeLayer {

  void addChild(CompositeLayer childLayer);
  
  // TODO: Currently replaces entries instead of adding them - is this good?
  void addEntries(CompositeLayerEntry entries);

  void paint(PaintCanvas canvas);

  PositionValue positioning();

  float posX();

  float posY();

  int zIndex();

  static CompositeLayer create(
    PositionValue positioning,
    float offsetX, float offsetY,
    int zIndex
  ) {
    return new CompositeLayerImp(positioning, offsetX, offsetY, zIndex);
  }

}
