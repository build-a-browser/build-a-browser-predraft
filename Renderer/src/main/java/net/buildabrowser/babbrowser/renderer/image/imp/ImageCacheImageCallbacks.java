package net.buildabrowser.babbrowser.renderer.image.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;

public class ImageCacheImageCallbacks implements ProgressiveImageCallbacks {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageCacheImageCallbacks.class);

  private final ImageCacheEntryImp imageEntry;
  private final GlobalObject globalObject;

  public ImageCacheImageCallbacks(
    ImageCacheEntryImp imageEntry,
    GlobalObject globalObject
  ) {
    this.imageEntry = imageEntry;
    this.globalObject = globalObject;
  }

  @Override
  public void onImageUpdate() {
    queueTask(() -> imageEntry.markUpdate());
  }

  @Override
  public void onImageDone() {
    queueTask(() -> imageEntry.markDone());
  }

  @Override
  public void onImageFailure(Exception e) {
    queueTask(() -> imageEntry.markDone());
    LOGGER.error("An error occured while loading the image!", e);
  }

  private void queueTask(Runnable task) {
    EventLoop.queueGlobalTask(TaskSource.DOM, globalObject, task);
  }

}
