package net.buildabrowser.babbrowser.renderer.content;

import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class ReEntrantContent implements BoxContent {

  private static final ReEntrantContent INSTANCE = new ReEntrantContent();

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    throw new IllegalStateException("Re-Entrant Layout Cycle Detected");
  }

  @Override
  public void positionLayers(
    UnmanagedBoxFragment<?> fragment,
    float layerX, float layerY
  ) {
    throw new IllegalStateException("Reached unreachable code");
  }

  public static ReEntrantContent instance() {
    return INSTANCE;
  }

}
