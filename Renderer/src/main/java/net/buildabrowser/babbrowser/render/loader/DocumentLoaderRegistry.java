package net.buildabrowser.babbrowser.render.loader;

import net.buildabrowser.babbrowser.render.loader.imp.DocumentLoaderRegistryImp;

public interface DocumentLoaderRegistry {

  void register(String mimeType, DocumentLoader loader);

  DocumentLoader getByMimeType(String mimeType);

  public static DocumentLoaderRegistry create() {
    return new DocumentLoaderRegistryImp();
  }
  
}
