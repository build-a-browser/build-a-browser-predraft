package net.buildabrowser.babbrowser.browser.render.paint;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public interface PaintCanvas {
  
  void pushPaint();

  void popPaint();

  void alterPaint(Consumer<Paint> func);

  void drawBox( float x,  float y,  float w,  float h);

  void drawText( float x,  float y, String text);

  // TODO: Replace with something more portable
  void drawImage( float x,  float y, BufferedImage image);

  void drawImage( float i,  float j,  float width,  float height, BufferedImage image);

  FontMetrics fontMetrics();

}
