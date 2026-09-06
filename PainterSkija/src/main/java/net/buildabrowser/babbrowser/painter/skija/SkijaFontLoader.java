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

  private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
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
    List<Font> fonts = new ArrayList<>(4);
    FontStyle style = new FontStyle(options.weight(), FontWidth.NORMAL, FontSlant.UPRIGHT);
    for (FontFamily family: options.families()) {
      if (!(family instanceof SkijaFontFamily skijaFontFamily)) {
        throw new IllegalArgumentException("Attempt to pass non-skija font-family into Skija renderer!");
      }

      String fontName = skijaFontFamily.isGeneric() ?
        resolveGenericFamily(skijaFontFamily.name()) :
        skijaFontFamily.name();
      Typeface typeface = manager.matchFamilyStyle(fontName, style);
      if (typeface == null) continue;
      fonts.add(new Font(typeface, options.size()));
    }

    // TODO: Also include some default fallbacks, for other languages
    return new SkijaLoadedFont(fonts.toArray(Font[]::new), options);
  }

  private String resolveGenericFamily(String genericName) {
    if (!IS_WINDOWS) return genericName;

    return switch (genericName.toLowerCase()) {
      case "sans-serif" -> "Segoe UI";
      case "serif" -> "Times New Roman";
      case "monospace" -> "Consolas";
      default -> "Segoe UI";
    };
  }

  private static record SkijaFontFamily(String name, boolean isGeneric) implements FontFamily {}
  
}
