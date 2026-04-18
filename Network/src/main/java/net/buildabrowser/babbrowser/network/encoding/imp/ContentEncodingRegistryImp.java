package net.buildabrowser.babbrowser.network.encoding.imp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.network.encoding.ContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.ContentDecoderFactory;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.network.encoding.decoders.IdentityContentDecoder;

public class ContentEncodingRegistryImp implements ContentEncodingRegistry {

  private final Map<String, ContentDecoderFactory> registeredDecoders = new HashMap<>();

  @Override
  public void registerDecoder(String name, ContentDecoderFactory decoderFactory) {
    registeredDecoders.put(name, decoderFactory);
  }

  @Override
  public ContentDecoder createDecoder(String name, ChunkHandler onChunk) throws IOException {
    ContentDecoderFactory decoderFactory = registeredDecoders.get(name);
    if (decoderFactory == null) return null;
    return decoderFactory.create(onChunk);
  }

  @Override
  public ContentDecoder createChainDecoder(List<String> names, ChunkHandler onChunk) throws IOException {
    ListIterator<String> nameIterator = names.listIterator(names.size());
    if (!nameIterator.hasPrevious()) {
      return new IdentityContentDecoder(onChunk);
    }
    
    ContentDecoder lastDecoder = createDecoder(nameIterator.previous(), onChunk);
    while (nameIterator.hasPrevious()) {
      lastDecoder = createDecoder(nameIterator.previous(), lastDecoder);
    }

    return lastDecoder;
  }

  @Override
  public Set<String> acceptedEncodings() {
    return registeredDecoders.keySet();
  }
  
}
