package net.buildabrowser.babbrowser.render.paint;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;

public interface BoxPainter {

  void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection);

  void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection);
  
}
