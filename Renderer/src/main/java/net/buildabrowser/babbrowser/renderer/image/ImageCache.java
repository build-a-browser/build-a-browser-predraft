package net.buildabrowser.babbrowser.renderer.image;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;
import net.buildabrowser.babbrowser.renderer.context.ScriptingContext;
import net.buildabrowser.babbrowser.renderer.image.imp.ImageCacheImp;

public interface ImageCache {
  
  LoadedImage getImage(
    URI imageURI,
    Invalidatable invalidatable,
    short invalidation
  );

  LoadedImage getImage(
    FetchResponse sourceResponse,
    Invalidatable invalidatable,
    short invalidation
  );

  void mark();

  void clean();

  static ImageCache create(ScriptingContext scriptingContext, ResourceLoader resourceLoader) {
    return new ImageCacheImp(scriptingContext, resourceLoader);
  }

}
