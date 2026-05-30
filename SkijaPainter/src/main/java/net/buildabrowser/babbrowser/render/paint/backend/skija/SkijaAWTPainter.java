package net.buildabrowser.babbrowser.render.paint.backend.skija;

import java.awt.Component;

import net.buildabrowser.babbrowser.render.paint.backend.CanvasCallbacks;
import net.buildabrowser.babbrowser.render.paint.backend.ComponentPainter;

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
