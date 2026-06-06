package net.buildabrowser.babbrowser.renderer.loader;

import net.buildabrowser.babbrowser.renderer.loader.imp.DocumentLoaderRegistryImp;
import net.buildabrowser.babbrowser.renderer.loader.loaders.HTMLDocumentLoader;

public interface DocumentLoaderRegistry {

  void register(String mimeType, DocumentLoader loader);

  DocumentLoader getByMimeType(String mimeType);

  public static DocumentLoaderRegistry create() {
    return new DocumentLoaderRegistryImp();
  }

  public static DocumentLoaderRegistry createDefault() {
    DocumentLoaderRegistry loaderRegistry = create();
    loaderRegistry.register("text/html", new HTMLDocumentLoader());
    return loaderRegistry;
  }
  
}
