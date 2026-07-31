package net.buildabrowser.babbrowser.renderer.loader.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.renderer.loader.DocumentLoader;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public class DocumentLoaderRegistryImp implements DocumentLoaderRegistry {

  private final Map<String, DocumentLoader> registeredLoaders = new HashMap<>();

  @Override
  public void register(String mimeType, DocumentLoader loader) {
    registeredLoaders.put(mimeType, loader);
  }

  @Override
  public DocumentLoader getByMimeType(String mimeType) {
    String adjustedMimeType = adjustMimeType(mimeType);
    return registeredLoaders.get(adjustedMimeType);
  }

  private String adjustMimeType(String mimeType) {
    // TODO: Properly handle part after ;
    int semiIndex = mimeType.indexOf(';');
    return semiIndex == -1 ?
      mimeType :
      mimeType.substring(0, semiIndex);
  }
  
}
