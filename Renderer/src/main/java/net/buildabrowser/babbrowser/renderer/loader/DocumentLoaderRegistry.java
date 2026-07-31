package net.buildabrowser.babbrowser.renderer.loader;

import net.buildabrowser.babbrowser.renderer.loader.imp.DocumentLoaderRegistryImp;
import net.buildabrowser.babbrowser.renderer.loader.loaders.HTMLDocumentLoader;
import net.buildabrowser.babbrowser.renderer.loader.loaders.ImageDocumentLoader;
import net.buildabrowser.babbrowser.renderer.loader.loaders.TextDocumentLoader;

public interface DocumentLoaderRegistry {

  void register(String mimeType, DocumentLoader loader);

  DocumentLoader getByMimeType(String mimeType);

  public static DocumentLoaderRegistry create() {
    return new DocumentLoaderRegistryImp();
  }

  public static DocumentLoaderRegistry createDefault() {
    DocumentLoaderRegistry loaderRegistry = create();
    ImageDocumentLoader imageDocumentLoader = new ImageDocumentLoader();
    TextDocumentLoader textDocumentLoader = new TextDocumentLoader();
    TextDocumentLoader jsDocumentLoader = textDocumentLoader;
    loaderRegistry.register("text/html", new HTMLDocumentLoader());
    loaderRegistry.register("image/png", imageDocumentLoader);
    loaderRegistry.register("image/jpeg", imageDocumentLoader);
    loaderRegistry.register("image/webp", imageDocumentLoader);
    // TODO: Animated gifs are not yet supported
    loaderRegistry.register("image/gif", imageDocumentLoader);
    loaderRegistry.register("image/bmp", imageDocumentLoader);
    loaderRegistry.register("image/x-icon", imageDocumentLoader);
    // TODO: SVG images don't actually work yet
    loaderRegistry.register("image/svg+xml", imageDocumentLoader);
    loaderRegistry.register("text/plain", textDocumentLoader);
    loaderRegistry.register("text/css", textDocumentLoader);
    loaderRegistry.register("text/javascript", jsDocumentLoader);
    loaderRegistry.register("text/ecmascript", jsDocumentLoader);
    loaderRegistry.register("text/x-javascript", jsDocumentLoader);
    loaderRegistry.register("application/javascript", jsDocumentLoader);
    loaderRegistry.register("application/ecmascript", jsDocumentLoader);
    loaderRegistry.register("application/x-javascript", jsDocumentLoader);
    return loaderRegistry;
  }

  // TODO: Download unsupported/unspecified mimes
  
}
