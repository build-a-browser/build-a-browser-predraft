package net.buildabrowser.babbrowser.painter.skija;

import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.Paint;

public class SkijaPaint implements Paint {

  private int color;
  private SkijaLoadedFont font;
  private boolean filled = true;
  private float strokeSize = 1f;

  @Override
  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public int getColor() {
    return this.color;
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

  @Override
  public void setFilled(boolean filled) {
    this.filled = filled;
  }

  @Override
  public boolean getFilled() {
    return this.filled;
  }

  @Override
  public void setStrokeSize(float strokeSize) {
    this.strokeSize = strokeSize;
  }

  @Override
  public float getStrokeSize() {
    return this.strokeSize;
  }

}
