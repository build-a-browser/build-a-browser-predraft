package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Transform;

// TODO: Find out why page rendering seems to be missing some elements present on the Skija painter
public class J2DPaintCanvas implements PaintCanvas {

  private final Deque<AffineTransform> transformStack = new ArrayDeque<>();

  private final J2DTransform transform;

  private Graphics2D graphics;
  private J2DPaint currentPaint = new J2DPaint();
  private J2DLoadedFont currentFont;

  public J2DPaintCanvas(Graphics2D graphics) {
    this.graphics = graphics;
    this.transform = new J2DTransform(graphics);
    // TODO: This is a bit hacky
    this.currentFont = new J2DLoadedFont(
      new Font[] { graphics.getFont() },
      new FontOptions(List.of(), 12, 500));
    currentPaint.setFont(currentFont);
  }

  @Override
  public void withPaint(
    Consumer<Paint> alterPaintFunc, Consumer<PaintCanvas> paintFunc
  ) {
    J2DPaint oldPaint = currentPaint;
    
    // TODO: Re-use paint instances
    J2DPaint paint = new J2DPaint();
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
  public void withTransform(
    Consumer<Transform> alterTransformFunc, Consumer<PaintCanvas> paintFunc
  ) {
    AffineTransform oldTransform = graphics.getTransform();
    AffineTransform newTransform = (AffineTransform) oldTransform.clone();
    graphics.setTransform(newTransform);
    alterTransformFunc.accept(transform);
    paintFunc.accept(this);
    graphics.setTransform(oldTransform);
  }

  @Override
  public void saveTransform(Consumer<PaintCanvas> paintFunc) {
    transformStack.push((AffineTransform) graphics.getTransform().clone());
    paintFunc.accept(this);
    graphics.setTransform(transformStack.pop());
  }

  @Override
  public void restoreTransform(Consumer<PaintCanvas> paintFunc) {
    AffineTransform oldTransform = graphics.getTransform();
    graphics.setTransform(transformStack.peek());
    paintFunc.accept(this);
    graphics.setTransform(oldTransform);
  }

  @Override
  public void withClip(
    float x, float y, float w, float h, Consumer<PaintCanvas> paintFunc
  ) {
    Shape oldClip = graphics.getClip();
    graphics.clipRect((int) x, (int) y, (int) w, (int) h);
    paintFunc.accept(this);
    graphics.setClip(oldClip);
  }

  @Override
  public void withShapedClip(Consumer<ClipShapeSpec> shapeFunc, Consumer<PaintCanvas> paintFunc) {
    Shape oldClip = graphics.getClip();
    J2DClipShapeSpec spec = new J2DClipShapeSpec();
    shapeFunc.accept(spec);
    Area clipArea = new Area(spec.shape());
    if (oldClip != null) {
      clipArea.intersect(new Area(oldClip));
    }
    graphics.setClip(clipArea);
    paintFunc.accept(this);
    graphics.setClip(oldClip);
  }

  @Override
  public void drawBox(float x, float y, float w, float h) {
    graphics.fillRect((int) x, (int) y, (int) w, (int) h);
  }

  @Override
  public void drawText(float x, float y, String text) {
    currentFont.drawText(x, y, text, graphics);
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

  private void syncPaint(J2DPaint paint) {
    graphics.setColor(new Color(paint.getColor(), true));
    graphics.setBackground(new Color(paint.getColor(), true));
    this.currentFont = paint.getFont();
  }
  
}
