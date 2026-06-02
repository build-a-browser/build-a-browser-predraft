package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public class Java2DPainter implements ComponentPainter<Component> {
  
  private final ResourceLoader resourceLoader = new J2DResourceLoader();

  @Override
  public ResourceLoader resourceLoader() {
    return this.resourceLoader;
  }

  @Override
  public PaintBitMap createPaintBitMap(int width, int height) {
    BufferedImage bitMapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    return new J2DBitMap(bitMapImage);
  }

  @Override
  public Component createComponent(CanvasCallbacks callbacks) {
    return new JPanel() {
      @Override
      public void doLayout() {
        super.doLayout();
        callbacks.layout(getWidth(), getHeight());
      }

      @Override
      public void paint(Graphics g) {
        callbacks.paint(new J2DPaintCanvas((Graphics2D) g));
      }
    };
  }

}
