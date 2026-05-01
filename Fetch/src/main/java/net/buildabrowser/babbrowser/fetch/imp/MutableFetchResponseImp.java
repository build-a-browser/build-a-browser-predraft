package net.buildabrowser.babbrowser.fetch.imp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
import net.buildabrowser.babbrowser.fetch.HeaderList;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;
import net.buildabrowser.babbrowser.network.URLUtil;

public class MutableFetchResponseImp implements MutableFetchResponse {

  private final List<URI> urlList = new ArrayList<>(4);
  private final HeaderList headerList = HeaderList.create();

  private int status = 200;
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
  public int status() {
    return this.status;
  }

  @Override
  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public HeaderList headerList() {
    return this.headerList;
  }

  @Override
  public FetchBody body() {
    return this.body;
  }

  @Override
  public void setBody(FetchBody body) {
    this.body = body;
  }

  @Override
  public URI locationURL(String requestFragment) throws URISyntaxException {
    if (!FetchUtil.isRedirectStatus(status)) return null;
    // TODO: Properly extract value
    String location = headerList.get("Location");
    if (location == null) return null;
    URI locationURI = URLUtil.createURL(url(), location);
    if (locationURI.getFragment() == null) {
      locationURI = new URI(
        locationURI.getScheme(),
        locationURI.getUserInfo(),
        locationURI.getHost(),
        locationURI.getPort(),
        locationURI.getPath(),
        locationURI.getQuery(),
        requestFragment);
    }

    return locationURI;
  }
  
}
