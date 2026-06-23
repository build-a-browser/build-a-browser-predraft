package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.shaper.Shaper;
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;

public class SkijaFontMetrics implements FontMetrics {

  private final Shaper shaper = Shaper.makeShapeDontWrapOrReorder();

  private final SkijaLoadedFont loadedFont;
  private final io.github.humbleui.skija.FontMetrics primaryMetrics;
  private final FontOptions fontOptions;

  public SkijaFontMetrics(
    Font[] rawFonts,
    SkijaLoadedFont loadedFont,
    FontOptions fontOptions
  ) {
    this.primaryMetrics = rawFonts[0].getMetrics();
    this.loadedFont = loadedFont;
    this.fontOptions = fontOptions;
  }

  @Override
  public float size() {
    return fontOptions.size();
  }

  @Override
  public int weight() {
    return fontOptions.weight();
  }

  @Override
  public float stringWidth(String text) {
    return loadedFont.splitText(shaper, 0, 0, text, (line, x, y) -> {});
  }

  @Override
  public float height() {
    return primaryMetrics.getHeight();
  }

  @Override
  public float xHeight() {
    return primaryMetrics.getXHeight();
  }

  @Override
  public float ascent() {
    return primaryMetrics.getAscent();
  }

}
