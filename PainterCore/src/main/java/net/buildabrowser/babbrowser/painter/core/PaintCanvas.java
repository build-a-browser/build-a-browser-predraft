package net.buildabrowser.babbrowser.painter.core;

import java.util.function.Consumer;

public interface PaintCanvas {

  void withPaint(Consumer<Paint> alterPaintFunc, Consumer<PaintCanvas> paintFunc);

  void withTransform(Consumer<Transform> alterTransformFunc, Consumer<PaintCanvas> paintFunc);

  void saveTransform(Consumer<PaintCanvas> paintFunc);

  void restoreTransform(Consumer<PaintCanvas> paintFunc);

  void withClip(float x, float y, float w, float h, Consumer<PaintCanvas> paintFunc);

  void drawBox(float x, float y, float w, float h);

  void drawText(float x, float y, String text);

  void drawImage(float x, float y, LoadedImage image);

  void drawImage(float x, float y, float w, float h, LoadedImage image);

  void drawBitMap(int x, int y, PaintBitMap bitMap); 

  FontMetrics fontMetrics();

  default void withPaintAndTransform(
    Consumer<Paint> alterPaintFunc,
    Consumer<Transform> alterTransformFunc,
    Consumer<PaintCanvas> paintFunc
  ) {
    withPaint(
      alterPaintFunc,
      c -> c.withTransform(
        alterTransformFunc,
        paintFunc));
  }

}
