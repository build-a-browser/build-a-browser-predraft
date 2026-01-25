package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public interface BoxContent {

  void prelayout(LayoutContext layoutContext);
  
  void layout(LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint);

  void layer(CompositeLayer layer);

  void paint(PaintCanvas canvas);

  void paintBackground(PaintCanvas canvas);

  default boolean isReplaced() {
    return false;
  }

}
