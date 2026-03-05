package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;

public interface BoxContent {

  default void fixupChildren() {}

  void prelayout(LayoutContext layoutContext, LayoutConstraint layoutConstraint);
  
  UnmanagedBoxFragment layout(LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  default boolean isReplaced() {
    return false;
  }

}
