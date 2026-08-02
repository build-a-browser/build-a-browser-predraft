package net.buildabrowser.babbrowser.renderer.content.grid;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.GenericFlexibleFixup;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class GridContent implements BoxContent {

  @Override
  public void fixupChildren(ElementBox rootBox) {
    GenericFlexibleFixup.fixupChildren(rootBox);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'layout'");
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    
  }
  
}
