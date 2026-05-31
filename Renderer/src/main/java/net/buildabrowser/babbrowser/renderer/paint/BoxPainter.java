package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;

public interface BoxPainter {

  void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection);

  void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection);
  
}
