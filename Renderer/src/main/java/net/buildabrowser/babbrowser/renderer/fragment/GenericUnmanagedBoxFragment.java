package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.UnreachableBoxPainter;

public class GenericUnmanagedBoxFragment extends UnmanagedBoxFragment<GenericUnmanagedBoxFragment> {

  private final BoxPainter<GenericUnmanagedBoxFragment> boxPainter;

  public GenericUnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box
  ) {
    super(width, height, inkWidth, inkHeight, box);
    this.boxPainter = UnreachableBoxPainter.create(box.element());
  }

  @Override
  protected BoxPainter<GenericUnmanagedBoxFragment> painter() {
    return boxPainter;
  }

  @Override
  protected EventHandler<GenericUnmanagedBoxFragment> eventHandler() {
    // TODO: Make a proper UnreachableEventHandler
    throw new IllegalStateException(
      "Reached unreachable GenericUnmanagedBoxFragment#eventHandler!");
  }
  
}
