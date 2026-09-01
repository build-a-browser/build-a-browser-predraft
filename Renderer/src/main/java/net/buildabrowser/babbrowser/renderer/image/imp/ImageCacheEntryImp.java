package net.buildabrowser.babbrowser.renderer.image.imp;

import java.lang.ref.WeakReference;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;

public class ImageCacheEntryImp {
  
  private boolean started;
  private ImageLoader loader;
  private SinglyLinkedList<ImageCacheListener> listeners;

  public LoadedImage getImage() {
    if (this.loader == null) return null;
    return this.loader.currentImage();
  }

  public void markStarted() {
    this.started = true;
  }

  public void setLoader(ImageLoader loader) {
    this.loader = loader;
  }

  public void markUpdate() {
    fireListeners();
  }

  public void markDone() {
    this.listeners = null;
  }

  public boolean started() {
    return this.started;
  }

  public void addListener(
    Invalidatable invalidatable,
    short invalidationLevel
  ) {
    SinglyLinkedList<ImageCacheListener> currentEntry = listeners;
    while (currentEntry != null) {
      ImageCacheListener currentListener = currentEntry.item();
      currentEntry = currentEntry.next();
      Invalidatable existingInvalidatable = currentListener.invalidatable().get();
      if (existingInvalidatable == null) continue;
      if (
        invalidatable == existingInvalidatable
        && currentListener.invalidationLevel == invalidationLevel
      ) return;
    }

    this.listeners = IntrusiveList.insert(
      this.listeners, 0,
      new SinglyLinkedList<>(new ImageCacheListener(
        new WeakReference<>(invalidatable), invalidationLevel)));
  }

  private void fireListeners() {
    SinglyLinkedList<ImageCacheListener> currentEntry = listeners;
    while (currentEntry != null) {
      ImageCacheListener currentListener = currentEntry.item();
      currentEntry = currentEntry.next();
      Invalidatable invalidatable = currentListener.invalidatable().get();
      if (invalidatable == null) continue;
      invalidatable.invalidate(currentListener.invalidationLevel());
    }
  }

  private static record ImageCacheListener(
    WeakReference<Invalidatable> invalidatable,
    short invalidationLevel
  ) {

  }

}
