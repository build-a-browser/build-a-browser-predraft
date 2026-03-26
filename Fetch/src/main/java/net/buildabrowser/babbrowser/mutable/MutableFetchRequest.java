package net.buildabrowser.babbrowser.mutable;

import java.net.URI;

import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.html.scripting.EnvironmentSettingsObject;

public interface MutableFetchRequest extends FetchRequest {

  void setMethod(String method);

  void setClient(EnvironmentSettingsObject client);
  
  void setURL(URI url);
  
}
