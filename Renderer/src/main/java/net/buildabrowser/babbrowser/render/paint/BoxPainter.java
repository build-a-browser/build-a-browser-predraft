package net.buildabrowser.babbrowser.render.paint;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;

public interface BoxPainter {

  void paint(BoxFragment fragment, PaintCanvas canvas);

  void paintBackground(BoxFragment fragment, PaintCanvas canvas);
  
}
