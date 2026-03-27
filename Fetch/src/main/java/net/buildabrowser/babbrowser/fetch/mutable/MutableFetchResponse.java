package net.buildabrowser.babbrowser.fetch.mutable;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchResponse;

public interface MutableFetchResponse extends FetchResponse {

  void setBody(FetchBody fetchBody);
  
}
