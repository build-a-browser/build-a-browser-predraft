package net.buildabrowser.babbrowser.browser.render.layout;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.imp.StackingContextImp;

public interface StackingContext {
  
  void defer(LayoutContext referenceContext, ElementBox box);

  void add(PosRefBoxFragment fragment);

  CompositeLayer layoutDeferred();

  static StackingContext create(int width, int height) {
    return new StackingContextImp(width, height);
  }

}