package net.buildabrowser.babbrowser.fetch.mutable;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.fetch.FetchRequest;

public interface MutableFetchRequest extends FetchRequest {

  void setMethod(String method);

  void setClient(FetchClient client);
  
  void setURL(URI url);
  
}
