package net.buildabrowser.babbrowser.renderer.paint;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;

public interface BoxPainter<T extends BoxFragment<T>> {

  void paint(T fragment, PaintCanvas canvas, VpIntersection vpIntersection);

  void paintBackground(T fragment, PaintCanvas canvas, VpIntersection vpIntersection);
  
}
