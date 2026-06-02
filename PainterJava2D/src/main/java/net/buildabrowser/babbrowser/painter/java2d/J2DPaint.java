package net.buildabrowser.babbrowser.painter.java2d;

import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.Paint;

public class J2DPaint implements Paint {

  private int color;
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
