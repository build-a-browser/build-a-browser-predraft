package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;

import net.buildabrowser.babbrowser.html.scripting.EnvironmentSettingsObject;
import net.buildabrowser.babbrowser.mutable.MutableFetchRequest;

public class MutableFetchRequestImp implements MutableFetchRequest {

  private String method;
  private EnvironmentSettingsObject client;
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
  public EnvironmentSettingsObject client() {
    return this.client;
  }

  @Override
  public void setClient(EnvironmentSettingsObject client) {
    this.client = client;
  }

  @Override
  public URI currentURL() {
    return this.url;
  }

}
