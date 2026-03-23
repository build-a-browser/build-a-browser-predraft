package net.buildabrowser.babbrowser.fetch;

import net.buildabrowser.babbrowser.fetch.imp.FetchEngineImp;

public interface FetchEngine {
  
  void fetch(FetchParameters fetchParameters);

  static FetchEngine create(FetchBackend backend) {
    return new FetchEngineImp(backend);
  }

}
