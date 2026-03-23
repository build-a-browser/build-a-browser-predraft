package net.buildabrowser.babbrowser.fetch;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.imp.MutableFetchRequestImp;
import net.buildabrowser.babbrowser.mutable.MutableFetchRequest;

public interface FetchRequest {
  
  URI url();

  URI currentURL();

  String method();

  static MutableFetchRequest createMutable() {
    return new MutableFetchRequestImp();
  }

}
