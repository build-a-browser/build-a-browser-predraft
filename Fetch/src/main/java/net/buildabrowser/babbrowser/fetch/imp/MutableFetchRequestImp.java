package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.mutable.MutableFetchRequest;

public class MutableFetchRequestImp implements MutableFetchRequest {

  private URI url;
  private String method;
  
  @Override
  public URI url() {
    return this.url;
  }

  @Override
  public URI currentURL() {
    return this.url;
  }

  @Override
  public void setURL(URI url) {
    this.url = url;
  }

  @Override
  public String method() {
    return this.method;
  }

  @Override
  public void setMethod(String method) {
    this.method = method;
  }

}
