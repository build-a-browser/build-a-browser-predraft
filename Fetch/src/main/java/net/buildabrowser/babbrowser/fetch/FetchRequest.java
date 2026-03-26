package net.buildabrowser.babbrowser.fetch;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.imp.MutableFetchRequestImp;
import net.buildabrowser.babbrowser.html.scripting.EnvironmentSettingsObject;
import net.buildabrowser.babbrowser.mutable.MutableFetchRequest;

public interface FetchRequest {

  String method();
  
  URI url();

  EnvironmentSettingsObject client();

  URI currentURL();

  static MutableFetchRequest createMutable() {
    return new MutableFetchRequestImp();
  }

}
