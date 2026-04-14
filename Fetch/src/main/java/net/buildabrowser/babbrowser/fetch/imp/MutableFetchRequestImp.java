package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public class MutableFetchRequestImp implements MutableFetchRequest {

  private List<URI> urlList = new ArrayList<>(4);

  private String method;
  private FetchClient client;

  @Override
  public String method() {
    return this.method;
  }

  @Override
  public void setMethod(String method) {
    this.method = method;
  }
  
  @Override
  public URI url() {
    return urlList.getFirst();
  }

  @Override
  public void setURL(URI url) {
    urlList.add(url);
  }

  @Override
  public FetchClient client() {
    return this.client;
  }

  @Override
  public void setClient(FetchClient client) {
    this.client = client;
  }

  @Override
  public List<URI> urlList() {
    return this.urlList;
  }

  @Override
  public URI currentURL() {
    return urlList.getLast();
  }

}
