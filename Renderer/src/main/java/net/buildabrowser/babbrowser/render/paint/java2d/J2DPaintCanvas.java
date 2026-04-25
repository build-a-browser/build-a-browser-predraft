package net.buildabrowser.babbrowser.render.paint.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.render.paint.FontMetrics;
import net.buildabrowser.babbrowser.render.paint.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.Paint;
import net.buildabrowser.babbrowser.render.paint.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.FontLoader.FontOptions;

public class J2DPaintCanvas implements PaintCanvas {

  private final Deque<J2DPaint> paintStack = new ArrayDeque<>();
  private final Deque<Shape> clipStack = new ArrayDeque<>();
  private final Graphics2D graphics;

  private float currentTranslateX, currentTranslateY;
  private J2DLoadedFont font;

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
    graphics.fillRect((int) x, (int) y, (int) w, (int) h);
  }

  @Override
  public void drawText(float x, float y, String text) {
    font.drawText(x, y, text, graphics);
  }

  @Override
  public void drawImage(float x, float y, LoadedImage image) {
    graphics.drawImage(((J2DLoadedImage) image).image(), (int) x, (int) y, (int) image.width(), (int) image.height(), null);
  }

  @Override
  public void drawImage(float x, float y, float w, float h, LoadedImage image) {
    graphics.drawImage(((J2DLoadedImage) image).image(), (int) x, (int) y, (int) w, (int) h, null);
  }

  @Override
  public void drawBitMap(int x, int y, PaintBitMap bitMap) {
    BufferedImage image = ((J2DBitMap) bitMap).image();
    graphics.drawImage(image, x, y, (int) image.getWidth(), image.getHeight(), null);
  }

  @Override
  public FontMetrics fontMetrics() {
    return new J2DFontMetrics(graphics.getFontMetrics(), new FontOptions(List.of(), 0, 0));
  }

  @Override
  public void clip(float x, float y, float w, float h) {
    if (graphics.getClip() != null) {
      clipStack.add(graphics.getClip());
    }
    graphics.clipRect((int) x, (int) y, (int) w, (int) h);
  }

  @Override
  public void unclip() {
    if (!clipStack.isEmpty()) {
      graphics.setClip(clipStack.pop());
    }
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

    this.font = paint.getFont();
  }
  
}
