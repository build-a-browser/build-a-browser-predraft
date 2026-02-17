package net.buildabrowser.babbrowser.browser.render.paint.skija;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.FontSlant;
import io.github.humbleui.skija.FontStyle;
import io.github.humbleui.skija.FontWeight;
import io.github.humbleui.skija.FontWidth;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.types.Rect;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedImage;
import net.buildabrowser.babbrowser.browser.render.paint.Paint;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class SkijaPaintCanvas implements PaintCanvas {

  private static final FontMgr manager = FontMgr.getDefault();
 
  private final Deque<SkijaPaint> paintStack = new ArrayDeque<>();

  private final Canvas canvas;
  private final io.github.humbleui.skija.Paint rawPaint;
  private final Font font;
  private final FontMetrics metrics;

  float currentTranslateX, currentTranslateY;

  public SkijaPaintCanvas(Canvas canvas) {
    this.canvas = canvas;
    this.rawPaint = new io.github.humbleui.skija.Paint();
    paintStack.push(new SkijaPaint());

    FontStyle style = new FontStyle(FontWeight.NORMAL, FontWidth.NORMAL, FontSlant.UPRIGHT);
    this.font = new Font(manager.matchFamilyStyle(null, style), 12);
    this.metrics = new SkijaFontMetrics(font);
  }

  @Override
  public void pushPaint() {
    SkijaPaint paint = new SkijaPaint();
    SkijaPaint parentPaint = paintStack.peek();
    paintStack.push(paint);
    paint.setOffset(parentPaint.offsetX(), parentPaint.offsetY());
    paint.setColor(parentPaint.getColor());
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
    canvas.drawRect(new Rect(x, y, x + w, y + h), rawPaint);
  }

  @Override
  public void drawText(float x, float y, String text) {
    if (text.isEmpty()) return;
    short[] glyphs = font.getStringGlyphs(text);
    float[] glyphWidths = font.getWidths(glyphs);
    float[] glyphPositions = new float[glyphWidths.length];
    float distance = 0;
    for (int i = 0; i < glyphWidths.length; i++) {
      glyphPositions[i] = distance;
      distance += glyphWidths[i];
    }

    TextBlob textBlob = TextBlob.makeFromPosH(glyphs, glyphPositions, metrics.fontHeight(), font);
    canvas.drawTextBlob(textBlob, x, y, rawPaint);
  }

  @Override
  public void drawImage(float x, float y, LoadedImage image) {
    drawImage(x, y, image.width(), image.height(), image);
  }

  @Override
  public void drawImage(float x, float y, float width, float height, LoadedImage image) {
    Rect rect = Rect.makeXYWH(x, y, width, height);
    canvas.drawImageRect(((SkijaLoadedImage) image).image(), rect, rawPaint);
  }

  @Override
  public FontMetrics fontMetrics() {
    return metrics;
  }

  private void postPaintUpdate() {
    SkijaPaint paint = paintStack.peek();
    rawPaint.setColor(paint.getColor());

    canvas.translate(
      paint.offsetX() - currentTranslateX,
      paint.offsetY() - currentTranslateY);
    currentTranslateX = paint.offsetX();
    currentTranslateY = paint.offsetY();
  }
  
}
