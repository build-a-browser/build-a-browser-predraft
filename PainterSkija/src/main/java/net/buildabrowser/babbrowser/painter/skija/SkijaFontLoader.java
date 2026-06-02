package net.buildabrowser.babbrowser.painter.skija;

import java.util.ArrayList;
import java.util.List;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.FontSlant;
import io.github.humbleui.skija.FontStyle;
import io.github.humbleui.skija.FontWidth;
import io.github.humbleui.skija.Typeface;
import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;

public class SkijaFontLoader implements FontLoader {

  private static final FontMgr manager = FontMgr.getDefault();

  @Override
  public FontFamily monospace() {
    return new SkijaFontFamily("monospace", true);
  }

  @Override
  public FontFamily serif() {
    return new SkijaFontFamily("serif", true);
  }

  @Override
  public FontFamily sansSerif() {
    return new SkijaFontFamily("sans-serif", true);
  }

  @Override
  public FontFamily named(String name) {
    return new SkijaFontFamily(name, false);
  }

  // TODO: Also a way to register a new font

  @Override
  public LoadedFont load(FontOptions options) {
    // TODO: Respect the isGeneric flag. We need to distinguish between a generic monospace font, and a literal
    // font named "monospace", per the spec, so figure out how to do so in Skija
    List<Font> fonts = new ArrayList<>(4);
    FontStyle style = new FontStyle(options.weight(), FontWidth.NORMAL, FontSlant.UPRIGHT);
    for (FontFamily family: options.families()) {
      if (!(family instanceof SkijaFontFamily skijaFontFamily)) {
        throw new IllegalArgumentException("Attempt to pass non-skija font-family into Skija renderer!");
      }

      Typeface typeface = manager.matchFamilyStyle(skijaFontFamily.name(), style);
      if (typeface == null) continue;
      fonts.add(new Font(typeface, options.size()));
    }

    // TODO: Also include some default fallbacks, for other languages
    return new SkijaLoadedFont(fonts.toArray(Font[]::new), options);
  }

  private static record SkijaFontFamily(String name, boolean isGeneric) implements FontFamily {}
  
}
