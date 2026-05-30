package net.buildabrowser.babbrowser.render.box;

import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public interface BoxContent {

  default void fixupChildren() {}

  default void computeIntrinsics() {}
  
  UnmanagedBoxFragment layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void positionLayers(float layerX, float layerY);

  ElementBox rootBox();

  EventHandler eventHandler();

  // TODO: Handle this more properly later
  default boolean computesOwnBorder() {
    return false;
  }

  default boolean isReplaced() {
    return false;
  }

}
