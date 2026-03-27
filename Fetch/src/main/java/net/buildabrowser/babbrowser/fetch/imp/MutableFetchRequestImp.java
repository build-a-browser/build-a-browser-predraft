package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public class MutableFetchRequestImp implements MutableFetchRequest {

  private String method;
  private FetchClient client;
  private URI url;

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
    return this.url;
  }

  @Override
  public void setURL(URI url) {
    this.url = url;
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
  public URI currentURL() {
    return this.url;
  }

}
