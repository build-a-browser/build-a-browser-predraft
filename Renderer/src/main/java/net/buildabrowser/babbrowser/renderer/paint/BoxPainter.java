package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;

public interface BoxPainter {

  void paint(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  void paintBackground(BoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection);
  
}
