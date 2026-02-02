package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.imp.StackingContextImp;

public interface StackingContext {
  
  void defer(PosRefBoxFragment fragment);

  StackingContext start();

  void end(PosRefBoxFragment fragment);

  void layoutDeferred(CompositeLayer baseLayer);

  static StackingContext create() {
    return new StackingContextImp();
  }

}