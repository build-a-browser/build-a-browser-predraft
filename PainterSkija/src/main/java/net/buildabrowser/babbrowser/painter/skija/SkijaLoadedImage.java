package net.buildabrowser.babbrowser.painter.skija;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.EncoderPNG;
import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;

public record SkijaLoadedImage(Image image) implements LoadedImage {

  @Override
  public int width() {
    return image.getWidth();
  }

  @Override
  public int height() {
    return image.getHeight();
  }

  @Override
  public InputStream streamData() throws IOException {
    try (Data data = EncoderPNG.encode(image)) {
      if (data == null) {
        throw new IOException("Could not create input stream for image!");
      }
      
      return new ByteArrayInputStream(data.getBytes());
    }
  }
  
}