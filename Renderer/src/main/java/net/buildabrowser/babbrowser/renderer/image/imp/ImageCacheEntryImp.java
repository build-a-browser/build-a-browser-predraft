package net.buildabrowser.babbrowser.renderer.image.imp;

import java.lang.ref.WeakReference;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;

public class ImageCacheEntryImp {
  
  private LoadedImage loadedImage;
  private SinglyLinkedList<ImageCacheListener> listeners;

  public LoadedImage getImage() {
    return this.loadedImage;
  }

  public void setLoadedImage(LoadedImage loadedImage) {
    this.loadedImage = loadedImage;
    fireListeners();
    this.listeners = null;
  }

  public boolean ongoing() {
    return this.listeners != null;
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
      if (invalidatable == null) continue;
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
