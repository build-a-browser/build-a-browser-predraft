package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.renderer.content.common.BorderUtil;
import net.buildabrowser.babbrowser.renderer.content.common.PaddingUtil;
import net.buildabrowser.babbrowser.renderer.event.EventHandler.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public interface BoxContent {

  default void fixupChildren() {}

  default void computeIntrinsics() {}

  default void computeMeasures(ElementBox box, LayoutConstraint referenceConstraint) {
    PaddingUtil.computePadding(box, referenceConstraint);
    BorderUtil.computeBorder(box);
  }
  
  UnmanagedBoxFragment<?> layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void positionLayers(float layerX, float layerY);

  ElementBox rootBox();

  // TODO: The generic should be on BoxContent itself, but I don't
  // feel like adding generics everywhere right now
  default <T extends BoxContent> EventHandlerResponse withFocusEventHandler(
    FocusEventHandlerFunc<T> withHandlerFunc
  ) {
    return EventHandlerResponse.UNHANDLED;
  }

  default boolean isReplaced() {
    return false;
  }

  default boolean hasCustomContent() {
    return false;
  }

  interface FocusEventHandlerFunc<T extends BoxContent> {
    EventHandlerResponse apply(
      FocusEventHandler<T> eventHandler, T content
    );
  }

}
