package net.buildabrowser.babbrowser.render.composite;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.composite.imp.CompositeLayerImp;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.backend.Painter;

public interface CompositeLayer {

  void addChild(CompositeLayer childLayer);
  
  // TODO: Currently replaces entries instead of adding them - is this good?
  void addEntries(CompositeLayerEntry entries);

  void repaint(int[] viewport);

  void draw(PaintCanvas canvas, int[] viewport);

  PositionValue positioning();

  float posX();

  float posY();

  int zIndex();

  List<CompositeLayer> childLayers();

  CompositeLayerEntry entries();

  static CompositeLayer create(
    Painter painter,
    PositionValue positioning,
    float offsetX, float offsetY,
    int zIndex
  ) {
    return new CompositeLayerImp(
      painter, positioning, offsetX, offsetY, zIndex);
  }

}
