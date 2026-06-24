package net.buildabrowser.babbrowser.painter.skija;

import static org.lwjgl.util.harfbuzz.HarfBuzz.HB_MEMORY_MODE_DUPLICATE;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_blob_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_blob_destroy;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_face_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_face_destroy;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_create;
import static org.lwjgl.util.harfbuzz.HarfBuzz.hb_font_set_scale;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import io.github.humbleui.skija.StreamAsset;
import io.github.humbleui.skija.Typeface;

public final class SkijaHarfBuzzLoader {

  private SkijaHarfBuzzLoader() {}

  public static long loadHarfBuzzFont(Typeface typeface, float fontSize) {
    try (StreamAsset fontData = typeface.openStream()) {
      int length = (int) fontData.getLength();
      byte[] fontBytes = new byte[length];
      fontData.read(fontBytes, length);

      ByteBuffer buffer = MemoryUtil.memAlloc(length);
      try {
        buffer.put(fontBytes).flip();

        long blob = hb_blob_create(buffer, HB_MEMORY_MODE_DUPLICATE, 0L, null);
        if (blob == 0L) throw new RuntimeException("Failed to create HarfBuzz blob.");

        long face = hb_face_create(blob, 0);
        long hbFont = hb_font_create(face);

        int scale = (int) (fontSize * 64);
        hb_font_set_scale(hbFont, scale, scale);

        hb_blob_destroy(blob);
        hb_face_destroy(face);
      
        return hbFont;
      } finally {
        MemoryUtil.memFree(buffer);
      }
    }
  }

}