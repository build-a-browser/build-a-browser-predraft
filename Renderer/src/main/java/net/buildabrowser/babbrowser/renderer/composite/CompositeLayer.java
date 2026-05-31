package net.buildabrowser.babbrowser.renderer.composite;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.renderer.composite.imp.CompositeLayerImp;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.paint.backend.Painter;

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

  boolean layerActive();

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
