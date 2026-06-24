package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontHinting;

public class SkijaFontEntry {
  
  private final Font font;
  private long harfBuzzFont;

  public SkijaFontEntry(Font font) {
    this.font = font;
    font.setSubpixel(true);
    font.setMetricsLinear(true);
    font.setHinting(FontHinting.NONE);
  }

  public Font font() {
    return this.font;
  }

  // TODO: Need to free eventually
  public long harfBuzzFont() {
    if (harfBuzzFont == 0) {
      this.harfBuzzFont = SkijaHarfBuzzLoader.loadHarfBuzzFont(
        font.getTypeface(), font.getSize());
    }

    return this.harfBuzzFont;
  }

}
