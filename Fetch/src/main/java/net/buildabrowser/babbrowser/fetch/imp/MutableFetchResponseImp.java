package net.buildabrowser.babbrowser.fetch.imp;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public class MutableFetchResponseImp implements MutableFetchResponse {

  private FetchBody body;

  @Override
  public FetchBody body() {
    return this.body;
  }

  @Override
  public void setBody(FetchBody body) {
    this.body = body;
  }
  
}
