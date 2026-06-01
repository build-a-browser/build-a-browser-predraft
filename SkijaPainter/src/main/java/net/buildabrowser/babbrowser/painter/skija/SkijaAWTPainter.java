package net.buildabrowser.babbrowser.painter.skija;

import java.awt.Component;

import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;

public class SkijaAWTPainter extends SkijaPainter implements ComponentPainter<Component> {

  private final boolean isSoftwareRendered;
  
  public SkijaAWTPainter(boolean isSoftwareRendered, boolean bitmapIsABitmap) {
    super(bitmapIsABitmap);
    this.isSoftwareRendered = isSoftwareRendered;
  }

  public Component createComponent(CanvasCallbacks callbacks) {
    if (isSoftwareRendered) {
      return new SkijaSoftwareCanvas(callbacks);
    } else {
      return new SkijaGPUCanvas(callbacks);
    }
  }

}
