package net.buildabrowser.babbrowser.renderer.paint.backend;

import java.util.function.Consumer;

public interface PaintCanvas {
  
  void pushPaint();

  void popPaint();

  void alterPaint(Consumer<Paint> func);

  void drawBox(float x, float y, float w, float h);

  void drawText(float x, float y, String text);

  void drawImage(float x, float y, LoadedImage image);

  void drawImage(float x, float y, float w, float h, LoadedImage image);

  void drawBitMap(int x, int y, PaintBitMap bitMap);

  void clip(float x, float y, float w, float h);

  void unclip();
 
  void mark();

  void unmark();

  void withMark(Consumer<PaintCanvas> withMarkCallback);

  FontMetrics fontMetrics();

}
