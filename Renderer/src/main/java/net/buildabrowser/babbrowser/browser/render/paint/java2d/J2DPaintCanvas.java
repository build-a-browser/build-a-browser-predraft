package net.buildabrowser.babbrowser.browser.render.paint.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.browser.render.paint.Paint;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class J2DPaintCanvas implements PaintCanvas {

  private final Deque<J2DPaint> paintStack = new ArrayDeque<>();
  private final Graphics2D graphics;

  float currentTranslateX, currentTranslateY;

  public J2DPaintCanvas(Graphics2D graphics) {
    this.graphics = graphics;
    paintStack.push(new J2DPaint());
  }

  @Override
  public void pushPaint() {
    J2DPaint paint = new J2DPaint();
    J2DPaint parentPaint = paintStack.peek();
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
    graphics.fillRect((int) x, (int) y, (int) w, (int) h);
  }

  @Override
  public void drawText(float x, float y, String text) {
    graphics.drawChars(text.toCharArray(), 0, text.length(), (int) x, (int) (y + fontMetrics().fontHeight()));
  }

  @Override
  public void drawImage(float x, float y, BufferedImage image) {
    graphics.drawImage(image, (int) x, (int) y, image.getWidth(), image.getHeight(), null);
  }

  @Override
  public void drawImage(float x, float y, float width, float height, BufferedImage image) {
    graphics.drawImage(image, (int) x, (int) y, (int) width, (int) height, null);
  }

  @Override
  public FontMetrics fontMetrics() {
    return new J2DFontMetrics(graphics.getFontMetrics());
  }

  private void postPaintUpdate() {
    J2DPaint paint = paintStack.peek();
    graphics.setColor(new Color(paint.getColor(), true));
    graphics.setBackground(new Color(paint.getColor(), true));

    graphics.translate(
      paint.offsetX() - currentTranslateX,
      paint.offsetY() - currentTranslateY);
    currentTranslateX = paint.offsetX();
    currentTranslateY = paint.offsetY();
  }
  
}
