package net.buildabrowser.babbrowser.render.image.imp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.html.events.EventLoop;
import net.buildabrowser.babbrowser.html.events.TaskSource;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.image.ImageCache;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.ResourceLoader;

public class ImageCacheImp implements ImageCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageCacheImp.class);

  private final Map<URI, ImageCacheEntryImp> imageEntries = new HashMap<>();

  private final ScriptingContext scriptingContext;
  private final ResourceLoader resourceLoader;

  public ImageCacheImp(
    ScriptingContext scriptingContext,
    ResourceLoader resourceLoader
  ) {
    this.scriptingContext = scriptingContext;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public LoadedImage getImage(
    URI imageURI,
    Invalidatable invalidatable,
    InvalidationLevel invalidation
  ) {
    ImageCacheEntryImp imageEntry = imageEntries.computeIfAbsent(
      imageURI, _ -> new ImageCacheEntryImp());
    if (imageEntry.getImage() != null) {
      return imageEntry.getImage();
    }

    if (!imageEntry.ongoing()) {
      loadImage(imageURI, imageEntry);
    }

    imageEntry.addListener(invalidatable, invalidation);

    return null;
  }

  @Override
  public void mark() {
    // TODO: Currently no-op, would start recording what images are used this frame
  }

  @Override
  public void clean() {
    // TODO: Currently no-op, would reset the mark and discard any old image entries
    // that have not been used in a while
  }

  private void loadImage(
    URI imageSource,
    ImageCacheEntryImp imageEntry
  ) {
    MutableFetchRequest fetchRequest = FetchRequest.createMutable();
    fetchRequest.setMethod("GET");
    fetchRequest.appendURL(imageSource);
    fetchRequest.setClient(scriptingContext.environmentSettingsObject());

    FetchParameters fetchParameters = new FetchParameters();
    fetchParameters.request = fetchRequest;
    fetchParameters.processResponseConsumeBody = (response, success, bytes) -> {
      if (success) {
        GlobalObject globalObject = scriptingContext.environmentSettingsObject().globalObject();
        EventLoop.queueGlobalTask(TaskSource.DOM, globalObject,
          () -> loadImageFromBytes(bytes, imageEntry));
      }
    };

    scriptingContext.fetchEngine().fetch(fetchParameters);
  }

  private synchronized void loadImageFromBytes(
    byte[] bytes,
    ImageCacheEntryImp imageEntry
  ) {
    try {
      // TODO: Also need to handle SVG
      LoadedImage image = resourceLoader.loadImage(new ByteArrayInputStream(bytes));
      imageEntry.setLoadedImage(image);
    } catch (IOException | IllegalArgumentException e) {
      LOGGER.error("An error occured while loading the image!", e);
    }
  }
  
}
