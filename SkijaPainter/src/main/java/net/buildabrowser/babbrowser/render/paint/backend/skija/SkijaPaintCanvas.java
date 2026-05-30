package net.buildabrowser.babbrowser.render.paint.backend.skija;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Matrix44;
import io.github.humbleui.types.Rect;
import net.buildabrowser.babbrowser.render.paint.backend.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.Paint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class SkijaPaintCanvas implements PaintCanvas {
 
  private final Deque<SkijaPaint> paintStack = new ArrayDeque<>();
  private final Deque<Matrix44> matrixStack = new ArrayDeque<>();
  private final Deque<SkijaPaint> markedPaintStack = new ArrayDeque<>();

  private final Canvas canvas;
  private final io.github.humbleui.skija.Paint rawPaint;

  private float currentTranslateX, currentTranslateY;
  private SkijaLoadedFont currentFont;

  public SkijaPaintCanvas(Canvas canvas) {
    this.canvas = canvas;
    this.rawPaint = new io.github.humbleui.skija.Paint();
    paintStack.push(new SkijaPaint());
  }

  @Override
  public void pushPaint() {
    SkijaPaint paint = new SkijaPaint();
    SkijaPaint parentPaint = paintStack.peek();
    paintStack.push(paint);
    paint.setOffset(parentPaint.offsetX(), parentPaint.offsetY());
    paint.setColor(parentPaint.getColor());
    paint.setFont(parentPaint.getFont());
    postPaintUpdate();
  }

  @Override
  public void popPaint() {
    paintStack.pop();
    postPaintUpdate();
  }

  @Override
  public void alterPaint(Consumer<Paint> func) {
    func.accept(paintStack.peek());
    postPaintUpdate();
  }

  @Override
  public void drawBox(float x, float y, float w, float h) {
    canvas.drawRect(Rect.makeXYWH(x, y, w, h), rawPaint);
  }

  @Override
  public void drawText(float x, float y, String text) {
    currentFont.drawText(x, y, text, canvas, rawPaint);
  }

  @Override
  public void drawImage(float x, float y, LoadedImage image) {
    drawImage(x, y, image.width(), image.height(), image);
  }

  @Override
  public void drawImage(float x, float y, float w, float h, LoadedImage image) {
    Rect rect = Rect.makeXYWH(x, y, w, h);
    canvas.drawImageRect(((SkijaLoadedImage) image).image(), rect, rawPaint);
  }

  @Override
  public void drawBitMap(int x, int y, PaintBitMap bitMap) {
    ((SkijaPaintBitMap) bitMap).draw(canvas, null, x, y);
  }

  @Override
  public FontMetrics fontMetrics() {
    return currentFont.metrics();
  }

  private void postPaintUpdate() {
    SkijaPaint paint = paintStack.peek();
    rawPaint.setColor(paint.getColor());

    canvas.translate(
      paint.offsetX() - currentTranslateX,
      paint.offsetY() - currentTranslateY);
    currentTranslateX = paint.offsetX();
    currentTranslateY = paint.offsetY();

    this.currentFont = paint.getFont();
  }

  @Override
  public void clip(float x, float y, float w, float h) {
    canvas.save();
    canvas.clipRect(Rect.makeXYWH(x, y, w, h));
  }

  @Override
  public void unclip() {
    Matrix44 matrix = canvas.getLocalToDevice();
    canvas.restore();
    canvas.setMatrix(matrix);
  }

  @Override
  public void mark() {
    matrixStack.add(canvas.getLocalToDevice());
    markedPaintStack.push(paintStack.getLast());
  }

  @Override
  public void unmark() {
    markedPaintStack.pop();
    canvas.setMatrix(matrixStack.pop());
  }

  @Override
  public void withMark(Consumer<PaintCanvas> withMarkCallback) {
    float oldTranslateX = currentTranslateX, oldTranslateY = currentTranslateY;
    currentTranslateX = 0; currentTranslateY = 0;

    Matrix44 oldMatrix = canvas.getLocalToDevice();
    canvas.setMatrix(matrixStack.peek());
    paintStack.push(markedPaintStack.getLast());
    postPaintUpdate();

    withMarkCallback.accept(this);

    paintStack.pop();
    postPaintUpdate();
    canvas.setMatrix(oldMatrix);

    currentTranslateX = oldTranslateX; currentTranslateY = oldTranslateY;
  }
  
}
