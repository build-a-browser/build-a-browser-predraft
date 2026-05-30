package net.buildabrowser.babbrowser.render;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public interface GraphicalDocumentRenderer extends DocumentRenderer {

  void resize(int width, int height);

  void draw(PaintCanvas context);

  void addRepaintListener(Runnable repaintListener);

}
