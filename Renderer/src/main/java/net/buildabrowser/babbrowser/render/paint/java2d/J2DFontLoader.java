package net.buildabrowser.babbrowser.render.paint.java2d;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.render.paint.FontLoader;
import net.buildabrowser.babbrowser.render.paint.LoadedFont;

public class J2DFontLoader implements FontLoader {

  @Override
  public FontFamily monospace() {
    return new J2DFontFamily(Font.MONOSPACED, true);
  }

  @Override
  public FontFamily serif() {
    return new J2DFontFamily(Font.SERIF, true);
  }

  @Override
  public FontFamily sansSerif() {
    return new J2DFontFamily(Font.SANS_SERIF, true);
  }

  @Override
  public FontFamily named(String name) {
    return new J2DFontFamily(name, false);
  }

  @Override
  public LoadedFont load(FontOptions options) {
    // TODO: Similar to the Skija one, need to respect generic flag
    List<Font> fonts = new ArrayList<>(4);
    for (FontFamily family: options.families()) {
      if (!(family instanceof J2DFontFamily j2dFontFamily)) {
        throw new IllegalArgumentException("Attempt to pass non-Java2D font-family into Java2D renderer!");
      }

      Font font = new Font(
        j2dFontFamily.name(),
        options.weight() > 550 ? Font.BOLD : Font.PLAIN,
        Math.round(options.size()));
      if (font.getFamily().equals(Font.DIALOG)) continue;

      fonts.add(font);
    }

    // TODO: Also include some default fallbacks, for other languages
    return new J2DLoadedFont(fonts.toArray(Font[]::new), options);
  }

  private static record J2DFontFamily(String name, boolean isGeneric) implements FontFamily {}
  
}
