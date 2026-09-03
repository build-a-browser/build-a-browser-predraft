package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.renderer.content.common.BorderUtil;
import net.buildabrowser.babbrowser.renderer.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.ContentEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public interface BoxContent {

  default void fixupChildren(ElementBox box) {}

  default void computeIntrinsics(ElementBox box) {}

  default void computeMeasures(ElementBox box, LayoutConstraint referenceConstraint) {
    PaddingUtil.computePadding(box, referenceConstraint);
    BorderUtil.computeBorder(box);
  }
  
  UnmanagedBoxFragment<?> layout(
    ElementBox box,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  );

  void positionLayers(
    UnmanagedBoxFragment<?> fragment, // TODO: Make fragment a generic?
    float layerX,
    float layerY
  );

  // TODO: The generic should be on BoxContent itself, but I don't
  // feel like adding generics everywhere right now
  default <T extends BoxContent> EventHandlerResponse withContentEventHandler(
    ElementBox box,
    ContentEventHandlerFunc<T> withHandlerFunc
  ) {
    return EventHandlerResponse.UNHANDLED;
  }

  default boolean isReplaced(ElementBox box) {
    return false;
  }

  default boolean hasCustomContent(ElementBox box) {
    return false;
  }

  interface ContentEventHandlerFunc<T extends BoxContent> {
    EventHandlerResponse apply(
      ContentEventHandler<T> eventHandler, T content
    );
  }

}
