package net.buildabrowser.babbrowser.fetch;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.imp.MutableFetchRequestImp;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;

public interface FetchRequest {

  String method();
  
  URI url();

  FetchClient client();

  URI currentURL();

  static MutableFetchRequest createMutable() {
    return new MutableFetchRequestImp();
  }

}
