package net.buildabrowser.babbrowser.renderer.image.imp;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class ImageReadRequest implements ReadRequest {

  private final ImageLoader imageLoader;
  private final ReadableStreamDefaultReader reader;

  public ImageReadRequest(ImageLoader imageLoader, ReadableStreamDefaultReader reader) {
    this.imageLoader = imageLoader;
    this.reader = reader;
  }

  @Override
  public void chunk(ByteBuffer chunk) {
    try {
      imageLoader.onChunk(chunk);
      reader.read(this);
    } catch (IOException e) {
      error(e);
    }
  }

  @Override
  public void close() {
    try {
      imageLoader.onDone();
    } catch (IOException e) {
      error(e);
    }
  }

  @Override
  public void error(Object e) {
    imageLoader.onFailure((Exception) e);
  }

}
