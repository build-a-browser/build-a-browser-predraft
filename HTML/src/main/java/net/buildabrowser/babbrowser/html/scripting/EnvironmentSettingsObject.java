package net.buildabrowser.babbrowser.html.scripting;

public record EnvironmentSettingsObject(
  RealmExecutionContext realmExecutionContext
) {

  public GlobalObject globalObject() {
    return realmExecutionContext().realm().globalObject();
  }

  public static EnvironmentSettingsObject create(RealmExecutionContext realmExecutionContext) {
    return new EnvironmentSettingsObject(realmExecutionContext);
  }

}
