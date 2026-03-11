package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.GlobalLayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;

public interface BoxContent {

  default void fixupChildren() {}

  default void computeIntrinsics(LayoutContext layoutContext) {}
  
  UnmanagedBoxFragment layout(LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void positionLayers(float layerX, float layerY, GlobalLayoutContext layoutContext);

  default boolean isReplaced() {
    return false;
  }

}
