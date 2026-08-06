package net.buildabrowser.babbrowser.renderer.composite.imp;

import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayer;
import net.buildabrowser.babbrowser.renderer.layout.stacking.LayerGenerator;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContextEntry;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContextPosition;

public class CompositeLayerGeneratorImp implements LayerGenerator<CompositeLayer> {
 
  private final Painter painter;

  public CompositeLayerGeneratorImp(Painter painter) {
    this.painter = painter;
  }

  @Override
  public CompositeLayer createLayer(
    StackingContextPosition position,
    int zIndexOrder,
    StackingContextEntry entries
  ) {
    CompositeLayer layer = CompositeLayer.create(painter, position, zIndexOrder);
    layer.addEntries(entries);
    return layer;
  }

  @Override
  public void addChild(CompositeLayer layer, CompositeLayer child) {
    layer.addChild(child);
  }

}
