package net.buildabrowser.babbrowser.renderer;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;

public interface GraphicalDocumentRenderer extends DocumentRenderer {

  void resize(int width, int height);

  void draw(PaintCanvas context);

  void addRepaintListener(Runnable repaintListener);

}
