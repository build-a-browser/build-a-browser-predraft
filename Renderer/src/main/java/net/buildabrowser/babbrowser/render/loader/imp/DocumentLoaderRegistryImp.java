package net.buildabrowser.babbrowser.render.loader.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.render.loader.DocumentLoader;
import net.buildabrowser.babbrowser.render.loader.DocumentLoaderRegistry;

public class DocumentLoaderRegistryImp implements DocumentLoaderRegistry {

  private final Map<String, DocumentLoader> registeredLoaders = new HashMap<>();

  @Override
  public void register(String mimeType, DocumentLoader loader) {
    registeredLoaders.put(mimeType, loader);
  }

  @Override
  public DocumentLoader getByMimeType(String mimeType) {
    return registeredLoaders.get(mimeType);
  }
  
}
