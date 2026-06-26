package net.buildabrowser.babbrowser.renderer.composite;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.composite.imp.CompositeLayerImp;
import net.buildabrowser.babbrowser.renderer.layout.StackingContextPosition;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public interface CompositeLayer {

  void addChild(CompositeLayer childLayer);
  
  // TODO: Currently replaces entries instead of adding them - is this good?
  void addEntries(CompositeLayerEntry entries);

  void repaint(VpIntersection vpIntersection);

  void draw(PaintCanvas canvas, VpIntersection vpIntersection);

  StackingContextPosition position();

  int zIndex();

  List<CompositeLayer> childLayers();

  boolean layerActive();

  CompositeLayerEntry entries();

  static CompositeLayer create(
    Painter painter,
    StackingContextPosition position,
    int zIndex
  ) {
    return new CompositeLayerImp(painter, position, zIndex);
  }

}
