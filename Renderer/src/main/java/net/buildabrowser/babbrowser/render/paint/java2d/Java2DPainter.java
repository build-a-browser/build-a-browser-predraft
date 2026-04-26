package net.buildabrowser.babbrowser.render.paint.java2d;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import net.buildabrowser.babbrowser.render.paint.CanvasCallbacks;
import net.buildabrowser.babbrowser.render.paint.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;

public class Java2DPainter implements Painter {
  
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
        callbacks.layout();
        super.doLayout();
      }

      @Override
      public void paint(Graphics g) {
        callbacks.paint(new J2DPaintCanvas((Graphics2D) g));
      }
    };
  }

}
