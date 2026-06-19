package net.buildabrowser.babbrowser.renderer;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;

public interface GraphicalDocumentRenderer extends DocumentRenderer {

  void resize(int width, int height);

  void draw(PaintCanvas context);

  EventForwardingTarget eventForwardingTarget();

  void addRepaintListener(Runnable repaintListener);

  void removeRepaintListener(Runnable repaintListener);

}
