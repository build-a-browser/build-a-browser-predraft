package net.buildabrowser.babbrowser.renderer.image.imp;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.image.ImageCache;
import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

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
    short invalidation
  ) {
    ImageCacheEntryImp imageEntry = imageEntries.computeIfAbsent(
      imageURI, _1 -> new ImageCacheEntryImp());
    if (imageEntry.getImage() != null) {
      return imageEntry.getImage();
    }

    if (!imageEntry.started()) {
      loadImage(imageURI, imageEntry);
    }

    imageEntry.addListener(invalidatable, invalidation);

    return null;
  }

  @Override
  public LoadedImage getImage(
    FetchResponse sourceResponse,
    Invalidatable invalidatable,
    short invalidation
  ) {
    URI imageURI = sourceResponse.url();
    ImageCacheEntryImp imageEntry = imageEntries.computeIfAbsent(
      imageURI, _1 -> new ImageCacheEntryImp());
    if (imageEntry.getImage() != null) {
      return imageEntry.getImage();
    }

    if (!imageEntry.started()) {
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
    imageEntry.markStarted();

    MutableFetchRequest fetchRequest = FetchRequest.createMutable();
    fetchRequest.setMethod("GET");
    fetchRequest.appendURL(imageSource);
    fetchRequest.setClient(scriptingContext.environmentSettingsObject());

    FetchParameters fetchParameters = new FetchParameters();
    fetchParameters.request = fetchRequest;
    fetchParameters.processResponse = response -> {
      loadImageFromResponse(response, imageEntry);
    };

    scriptingContext.fetchEngine().fetch(fetchParameters);
  }

  private void loadImageFromResponse(
    FetchResponse response,
    ImageCacheEntryImp imageEntry
  ) {
    imageEntry.markStarted();

    if (response.status() < 200 || response.status() > 399) {
      LOGGER.error("Could not load image: Invalid status {}!", response.status());
      return;
    }

    String contentType = response.headerList().get("Content-Type");
    if (contentType == null) {
      // TODO: Sniff the content type
      LOGGER.error("Could not load image: Could not determine Content-Type!");
      return;
    }

    ImageLoader imageLoader = resourceLoader.progressivelyLoadImage(
      response.headerList().get("Content-Type"),
      new ImageCacheImageCallbacks(imageEntry, scriptingContext.globalObject()),
      scriptingContext.globalObject()::runInParallel);
    imageEntry.setLoader(imageLoader);
    
    ReadableStreamDefaultReader reader = (ReadableStreamDefaultReader)
      response.body().stream().getReader(null);
    ReadRequest readRequest = new ImageReadRequest(imageLoader, reader);
    reader.read(readRequest);
  }
  
}
