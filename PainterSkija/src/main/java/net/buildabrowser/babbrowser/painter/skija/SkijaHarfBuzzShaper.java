package net.buildabrowser.babbrowser.painter.skija;

import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_add_utf8;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_destroy;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_get_glyph_infos;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_get_glyph_positions;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_buffer_guess_segment_properties;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_shape;

import org.lwjgl.util.harfbuzz.hb_glyph_info_t;
import org.lwjgl.util.harfbuzz.hb_glyph_position_t;

import io.github.humbleui.skija.TextBlob;

public final class SkijaHarfBuzzShaper {

  private SkijaHarfBuzzShaper() {}

  // Skija's built in shaping mechanisms are too slow, so use manual shaping instead
  // TODO: Need to support bidi, y positions, and stuff
  public static TextBlob shape(String text, SkijaFontEntry currentFont, float[] runSize) {
    long harfBuzzFont = currentFont.harfBuzzFont();
    long hbBuffer = hb_buffer_create();
    try {
      hb_buffer_add_utf8(hbBuffer, text, 0, -1);
      hb_buffer_guess_segment_properties(hbBuffer);
      hb_shape(harfBuzzFont, hbBuffer, null);
      
      hb_glyph_info_t.Buffer glyphInfos = hb_buffer_get_glyph_infos(hbBuffer);
      hb_glyph_position_t.Buffer glyphPositions = hb_buffer_get_glyph_positions(hbBuffer);
      
      int glyphCount = glyphInfos.limit();

      short[] glyphs = new short[glyphCount];
      float[] positions = new float[glyphCount];
      int runningPosition = 0;
      for (int i = 0; i < glyphCount; i++) {
        glyphs[i] = (short) glyphInfos.get(i).codepoint();
        positions[i] = (runningPosition + glyphPositions.get(i).x_offset()) / 64f;
        // TODO: Also check y-advance
        runningPosition += glyphPositions.get(i).x_advance();
      }

      runSize[0] = runningPosition / 64f;
      return TextBlob.makeFromPosH(
        glyphs, positions, 0, currentFont.font());
    } finally {
      hb_buffer_destroy(hbBuffer);
    }
  }

}
