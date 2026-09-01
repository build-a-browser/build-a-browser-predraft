package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.stream.ImageInputStream;

import net.buildabrowser.babbrowser.common.util.BufferUtil;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;

public class Java2DImageLoader implements ImageLoader, IIOReadUpdateListener, IIOReadProgressListener {
  
  private final PipedInputStream bufferIn = new PipedInputStream(64 * 1024);
  private final PipedOutputStream bufferOut;
  private final ProgressiveImageCallbacks callbacks;

  private LoadedImage currentImage;

  public Java2DImageLoader(
    String mimeType,
    ProgressiveImageCallbacks callbacks,
    Consumer<Runnable> threadRunner
  ) {
    this.callbacks = callbacks;
    this.bufferOut = createBufferOut();
    threadRunner.accept(() -> decodeImageCatch(mimeType));
  }

  @Override
  public void onChunk(ByteBuffer chunk) throws IOException {
    BufferUtil.writeBufferToStream(chunk, bufferOut);
  }

  @Override
  public void onDone() throws IOException {
    bufferOut.close();
  }

  @Override
  public void onFailure(Exception e) {
    this.currentImage = null;
    callbacks.onImageFailure(e);
    e.printStackTrace();

    try {
      bufferIn.close();
      bufferOut.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public LoadedImage currentImage() {
    return this.currentImage;
  }

  @Override
  public void passComplete(ImageReader source, BufferedImage theImage) {
    this.currentImage = new J2DLoadedImage(theImage);
    callbacks.onImageUpdate();
  }

  @Override
  public void imageUpdate(
    ImageReader source, BufferedImage theImage, int minX, int minY, int width, int height,
    int periodX, int periodY, int[] bands
  ) {
    this.currentImage = new J2DLoadedImage(theImage);
    callbacks.onImageUpdate();
  }

  @Override
  public void imageComplete(ImageReader source) {
    callbacks.onImageDone();
  }

  private void decodeImageCatch(String mimeType) {
    try {
      decodeImage(mimeType);
    } catch (IOException e) {
      onFailure(e);
    }
  }

  private void decodeImage(String mimeType) throws IOException {
    Iterator<ImageReader> readerIt = ImageIO.getImageReadersByMIMEType(mimeType);
    if (!readerIt.hasNext()) {
      onFailure(new IOException("Failed to load image: No compatible reader for Content-Type '" + mimeType + "'!"));
      return;
    }

    try (
      ImageInputStream imageInput = ImageIO.createImageInputStream(bufferIn)
    ) {
      ImageReader reader = readerIt.next();
      reader.setInput(imageInput);
      reader.addIIOReadUpdateListener(this);
      reader.addIIOReadProgressListener(this);
      BufferedImage image = reader.read(0);
      this.currentImage = new J2DLoadedImage(image);
      callbacks.onImageUpdate();
    }
  }

  private PipedOutputStream createBufferOut() {
    try {
      return new PipedOutputStream(bufferIn);
    } catch (IOException e) {
      callbacks.onImageFailure(e);
      return null;
    }
  }

  // A ton of unimportant stuff that Java forces us to implement
  @Override public void passStarted(ImageReader source, BufferedImage theImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {}
  @Override public void thumbnailPassStarted(ImageReader source, BufferedImage theThumbnail, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {}
  @Override public void thumbnailUpdate(ImageReader source, BufferedImage theThumbnail, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {}
  @Override public void thumbnailPassComplete(ImageReader source, BufferedImage theThumbnail) {}
  @Override public void sequenceStarted(ImageReader source, int minIndex) {}
  @Override public void sequenceComplete(ImageReader source) {}
  @Override public void imageStarted(ImageReader source, int imageIndex) {}
  @Override public void imageProgress(ImageReader source, float percentageDone) {}
  @Override public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) {}
  @Override public void thumbnailProgress(ImageReader source, float percentageDone) {}
  @Override public void thumbnailComplete(ImageReader source) {}
  @Override public void readAborted(ImageReader source) {}

}
