package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.renderer.content.common.BorderUtil;
import net.buildabrowser.babbrowser.renderer.content.common.PaddingUtil;
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

  default boolean isReplaced() {
    return false;
  }

  default boolean hasCustomContent() {
    return false;
  }

}
