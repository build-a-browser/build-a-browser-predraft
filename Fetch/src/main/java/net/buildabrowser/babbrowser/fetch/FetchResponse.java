package net.buildabrowser.babbrowser.fetch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.imp.MutableFetchResponseImp;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public interface FetchResponse {

  URI url();

  List<URI> urlList();

  int status();

  HeaderList headerList();

  FetchBody body();

  URI locationURL(String requestFragment) throws URISyntaxException;

  public static MutableFetchResponse createMutable() {
    return new MutableFetchResponseImp();
  }
  
  public static FetchResponse create(String status, HeaderList headerList, FetchBody body) {
    // TODO
    MutableFetchResponse response = new MutableFetchResponseImp();
    response.setBody(body);
    headerList.forEach(response.headerList()::append);
    return response;
  }

  public static FetchResponse createNetworkError() {
    // TODO
    return new MutableFetchResponseImp();
  }

}
