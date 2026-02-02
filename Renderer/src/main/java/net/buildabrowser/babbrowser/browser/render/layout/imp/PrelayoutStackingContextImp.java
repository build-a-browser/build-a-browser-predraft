package net.buildabrowser.babbrowser.browser.render.layout.imp;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;

public class PrelayoutStackingContextImp implements StackingContext {

  @Override
  public void defer(PosRefBoxFragment fragment) {
    // Don't defer
    fragment.box().content().prelayout(fragment.referenceLayoutContext());
  }

  @Override
  public StackingContext start() {
    // No-op
    return this;
  }

  @Override
  public void end(PosRefBoxFragment fragment) {
    // No-op
  }

  @Override
  public void layoutDeferred(CompositeLayer baseLayer) {
    // No-op
  }
  
}
