package net.buildabrowser.babbrowser.painter.backend.java2d;

import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.renderer.paint.backend.Paint;

public class J2DPaint implements Paint {

  private int color;
  private float offsetX;
  private float offsetY;
  private J2DLoadedFont loadedFont;

  @Override
  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public int getColor() {
    return this.color;
  }

  @Override
  public void incOffset(float x, float y) {
    this.offsetX += x;
    this.offsetY += y;
  }

  @Override
  public void setOffset(float x, float y) {
    this.offsetX = x;
    this.offsetY = y;
  }

  @Override
  public float offsetX() {
    return this.offsetX;
  }

  @Override
  public float offsetY() {
    return this.offsetY;
  }

  @Override
  public void setFont(LoadedFont loadedFont) {
    if (!(loadedFont instanceof J2DLoadedFont j2dLoadedFont)) {
      throw new IllegalArgumentException("Attempt to pass non-Java2D font into Java2D renderer!");
    }

    this.loadedFont = j2dLoadedFont;
  }

  @Override
  public J2DLoadedFont getFont() {
    return this.loadedFont;
  }

}
