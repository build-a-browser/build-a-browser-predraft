package net.buildabrowser.babbrowser.html.scripting;

import net.buildabrowser.babbrowser.fetch.FetchClient;
import net.buildabrowser.babbrowser.fetch.FetchDestinatation;

public record EnvironmentSettingsObject(
  RealmExecutionContext realmExecutionContext
) implements FetchClient {

  public GlobalObject globalObject() {
    return realmExecutionContext().realm().globalObject();
  }

  public static EnvironmentSettingsObject create(RealmExecutionContext realmExecutionContext) {
    return new EnvironmentSettingsObject(realmExecutionContext);
  }

  @Override
  public FetchDestinatation fetchDestinatation() {
    return globalObject();
  }

}
