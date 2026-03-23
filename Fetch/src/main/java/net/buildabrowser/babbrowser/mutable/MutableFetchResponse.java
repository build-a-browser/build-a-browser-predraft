package net.buildabrowser.babbrowser.mutable;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchResponse;

public interface MutableFetchResponse extends FetchResponse {

  void setBody(FetchBody fetchBody);
  
}
