package net.buildabrowser.babbrowser.painter.skija;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.github.humbleui.skija.Bitmap;
import io.github.humbleui.skija.Codec;
import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageInfo;
import net.buildabrowser.babbrowser.common.util.BufferUtil;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;

public class SkijaImageLoader implements ImageLoader {

  private final ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
  private final ProgressiveImageCallbacks callbacks;

  private LoadedImage currentImage;
  private boolean hasNewData;
  private Bitmap bitmap;
  private Exception queuedException;
  private boolean done;

  public SkijaImageLoader(
    ProgressiveImageCallbacks callbacks
  ) {
    this.callbacks = callbacks;
  }

  @Override
  public void onChunk(ByteBuffer chunk) throws IOException {
    BufferUtil.writeBufferToStream(chunk, bufferOut);
    callbacks.onImageUpdate();
    this.hasNewData = true;
  }

  @Override
  public void onDone() throws IOException {
    this.done = true;
    if (this.hasNewData) {
      this.currentImage = loadCurrentImage();
    }

    if (
      this.queuedException != null
    ) {
      callbacks.onImageFailure(queuedException);
    } else if (this.currentImage == null) {
      callbacks.onImageFailure(new IOException("No image loaded!"));
    } else {
      callbacks.onImageDone();
      bufferOut.close();
    }
  }

  @Override
  public void onFailure(Exception e) {
    this.currentImage = null;
    callbacks.onImageFailure(e);
    try {
      bufferOut.close();
    } catch (IOException e2) {
      e2.printStackTrace();
    }
  }

  @Override
  public LoadedImage currentImage() {
    if (this.hasNewData) {
      this.currentImage = loadCurrentImage();
      this.hasNewData = false;
    }

    return this.currentImage;
  }

  private LoadedImage loadCurrentImage() {
    byte[] currentBytes = bufferOut.toByteArray();
    try (Data data = Data.makeFromBytes(currentBytes);
      Codec codec = Codec.makeFromData(data)) {
      if (codec == null) return null;

      if (bitmap == null) {
        ImageInfo imageInfo = codec.getImageInfo();
        bitmap = new Bitmap();
        bitmap.allocPixels(imageInfo);
        bitmap.erase(0x00000000);
      }

      try {
          codec.readPixels(bitmap);
      } catch (Exception partialEx) {}
      this.currentImage = new SkijaLoadedImage(
        Image.makeRasterFromBitmap(bitmap));
      this.queuedException = null;
    } catch (Exception e) {
      this.queuedException = e;
      this.currentImage = null;
      if (done) {
        onFailure(e);
      }
    }

    return this.currentImage;
  }

}
