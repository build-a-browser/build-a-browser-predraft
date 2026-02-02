package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;

public interface BoxContent {

  void prelayout(LayoutContext layoutContext, LayoutConstraint option);
  
  UnmanagedBoxFragment layout(LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  default boolean isReplaced() {
    return false;
  }

}
