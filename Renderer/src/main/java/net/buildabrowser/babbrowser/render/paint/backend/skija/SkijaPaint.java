package net.buildabrowser.babbrowser.render.paint.backend.skija;

import net.buildabrowser.babbrowser.render.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.backend.Paint;

public class SkijaPaint implements Paint {

  private int color;
  private float offsetX;
  private float offsetY;
  private SkijaLoadedFont font;

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
  public void setFont(LoadedFont font) {
    if (!(font instanceof SkijaLoadedFont skijaFont)) {
      throw new IllegalArgumentException("Attempt to pass non-skija font into Skija renderer!");
    }
    this.font = skijaFont;
  }

  @Override
  public SkijaLoadedFont getFont() {
    return this.font;
  }

}
