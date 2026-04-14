package net.buildabrowser.babbrowser.fetch;

import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.fetch.imp.MutableFetchRequestImp;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public interface FetchRequest {

  String method();
  
  URI url();

  FetchClient client();

  List<URI> urlList();

  URI currentURL();

  static MutableFetchRequest createMutable() {
    return new MutableFetchRequestImp();
  }

}
