package net.buildabrowser.babbrowser.fetch.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.getFirst;
import static net.buildabrowser.babbrowser.common.util.CompatUtil.getLast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public class MutableFetchRequestImp implements MutableFetchRequest {

  private List<URI> urlList = new ArrayList<>(4);

  private String method;
  private FetchClient client;
  private RequestMode mode = RequestMode.NO_CORS;
  private RedirectMode redirectMode = RedirectMode.FOLLOW;
  private int redirectCount = 0;

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
    return getFirst(urlList);
  }

  @Override
  public void appendURL(URI url) {
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
  public RequestMode mode() {
    return this.mode;
  }

  @Override
  public void setMode(RequestMode mode) {
    this.mode = mode;
  }

  @Override
  public RedirectMode redirectMode() {
    return this.redirectMode;
  }

  @Override
  public void setRedirectMode(RedirectMode redirectMode) {
    this.redirectMode = redirectMode;
  }

  @Override
  public List<URI> urlList() {
    return this.urlList;
  }

  @Override
  public URI currentURL() {
    return getLast(urlList);
  }

  @Override
  public int redirectCount() {
    return this.redirectCount;
  }

  @Override
  public void increaseRedirectCount() {
    this.redirectCount++;
  }

}
