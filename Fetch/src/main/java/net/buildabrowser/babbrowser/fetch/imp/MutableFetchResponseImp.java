package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public class MutableFetchResponseImp implements MutableFetchResponse {

  private final List<URI> urlList = new ArrayList<>(4);

  private FetchBody body;

  @Override
  public URI url() {
    if (urlList.isEmpty()) return null;
    return urlList.getLast();
  }

  @Override
  public List<URI> urlList() {
    return this.urlList;
  }

  @Override
  public FetchBody body() {
    return this.body;
  }

  @Override
  public void setBody(FetchBody body) {
    this.body = body;
  }
  
}
