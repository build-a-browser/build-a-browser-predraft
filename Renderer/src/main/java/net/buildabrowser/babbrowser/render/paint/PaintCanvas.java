package net.buildabrowser.babbrowser.render.paint;

import java.util.function.Consumer;

public interface PaintCanvas {
  
  void pushPaint();

  void popPaint();

  void alterPaint(Consumer<Paint> func);

  void drawBox(float x, float y, float w, float h);

  void drawText(float x, float y, String text);

  void drawImage(float x, float y, LoadedImage image);

  void drawImage(float x, float y, float w, float h, LoadedImage image);

  void clip(float x, float y, float w, float h);

  void unclip();

  FontMetrics fontMetrics();

}
