package net.buildabrowser.babbrowser.browser.render.paint;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;

public interface BoxPainter {

  void paint(BoxFragment fragment, PaintCanvas canvas);

  void paintBackground(BoxFragment fragment, PaintCanvas canvas);
  
}
