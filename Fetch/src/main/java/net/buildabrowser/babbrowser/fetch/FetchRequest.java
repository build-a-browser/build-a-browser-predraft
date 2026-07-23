package net.buildabrowser.babbrowser.fetch;

import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.imp.MutableFetchRequestImp;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public interface FetchRequest {

  String method();
  
  URI url();

  HeaderList headerList();

  Object body();

  FetchClient client();

  RequestMode mode();

  RedirectMode redirectMode();

  List<URI> urlList();

  URI currentURL();

  int redirectCount();

  static MutableFetchRequest createMutable() {
    return new MutableFetchRequestImp();
  }

  static enum RequestMode {
    SAME_ORIGIN, CORS, NO_CORS, NAVIGATE, WEBSOCKET, WEBTRANSPORT;
  }

  static enum RedirectMode {
    FOLLOW, ERROR, MANUAL;
  }

}
