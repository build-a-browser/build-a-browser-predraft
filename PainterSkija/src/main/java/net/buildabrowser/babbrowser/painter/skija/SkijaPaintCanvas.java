package net.buildabrowser.babbrowser.painter.skija;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Matrix44;
import io.github.humbleui.types.Rect;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Transform;

public class SkijaPaintCanvas implements PaintCanvas {
 
  private final Deque<Matrix44> matrixStack = new ArrayDeque<>();

  private final io.github.humbleui.skija.Paint rawPaint = new io.github.humbleui.skija.Paint();
  private final Canvas canvas;
  private final SkijaTransform transform;

  private SkijaPaint currentPaint = new SkijaPaint();
  private SkijaLoadedFont currentFont;

  public SkijaPaintCanvas(Canvas canvas) {
    this.canvas = canvas;
    this.transform = new SkijaTransform(canvas);
    this.currentFont = SkijaLoadedFont.noFont();
    currentPaint.setColor(0xFFFFFFFF);
    currentPaint.setFont(currentFont);
    syncPaint(currentPaint);
  }

  @Override
  public void withPaint(
    Consumer<Paint> alterPaintFunc, Consumer<PaintCanvas> paintFunc
  ) {
    SkijaPaint oldPaint = currentPaint;
    
    // TODO: Re-use paint instances
    SkijaPaint paint = new SkijaPaint();
    paint.setColor(oldPaint.getColor());
    paint.setFont(oldPaint.getFont());
    alterPaintFunc.accept(paint);
    this.currentPaint = paint;
    syncPaint(paint);

    paintFunc.accept(this);

    this.currentPaint = oldPaint;
    syncPaint(currentPaint);
  }

  @Override
  public void withTransform(Consumer<Transform> alterTransformFunc, Consumer<PaintCanvas> paintFunc) {
    canvas.save();
    alterTransformFunc.accept(transform);
    paintFunc.accept(this);
    canvas.restore();
  }

  @Override
  public void saveTransform(Consumer<PaintCanvas> paintFunc) {
    matrixStack.push(canvas.getLocalToDevice());
    paintFunc.accept(this);
    canvas.setMatrix(matrixStack.pop());
  }

  @Override
  public void restoreTransform(Consumer<PaintCanvas> paintFunc) {
    Matrix44 oldMatrix = canvas.getLocalToDevice();
    canvas.setMatrix(matrixStack.peek());
    paintFunc.accept(this);
    canvas.setMatrix(oldMatrix);
  }

  @Override
  public void withClip(float x, float y, float w, float h, Consumer<PaintCanvas> paintFunc) {
    canvas.save();
    canvas.clipRect(Rect.makeXYWH(x, y, w, h));
    paintFunc.accept(this);
    canvas.restore();
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

  private void syncPaint(SkijaPaint paint) {
    rawPaint.setColor(paint.getColor());
    this.currentFont = paint.getFont();
  }
  
}
