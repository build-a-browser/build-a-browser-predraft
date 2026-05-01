package net.buildabrowser.babbrowser.render.image;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.render.context.ScriptingContext;
import net.buildabrowser.babbrowser.render.image.imp.ImageCacheImp;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.ResourceLoader;

public interface ImageCache {
  
  LoadedImage getImage(
    URI imageURI,
    Invalidatable invalidatable,
    InvalidationLevel invalidation
  );

  void mark();

  void clean();

  static ImageCache create(ScriptingContext scriptingContext, ResourceLoader resourceLoader) {
    return new ImageCacheImp(scriptingContext, resourceLoader);
  }

}
