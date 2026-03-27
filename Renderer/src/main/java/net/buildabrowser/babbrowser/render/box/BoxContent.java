package net.buildabrowser.babbrowser.render.box;

import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public interface BoxContent {

  default void fixupChildren() {}

  default void computeIntrinsics() {}
  
  UnmanagedBoxFragment layout(LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void positionLayers(float layerX, float layerY);

  default boolean isReplaced() {
    return false;
  }

}
