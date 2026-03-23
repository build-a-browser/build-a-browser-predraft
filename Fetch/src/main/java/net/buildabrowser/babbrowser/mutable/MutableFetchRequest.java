package net.buildabrowser.babbrowser.mutable;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.FetchRequest;

public interface MutableFetchRequest extends FetchRequest {
  
  void setURL(URI url);

  void setMethod(String method);
  
}
