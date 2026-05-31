package net.buildabrowser.babbrowser.renderer.context;

import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.html.scripting.EnvironmentSettingsObject;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;

public record ScriptingContext(
  FetchEngine fetchEngine,
  EnvironmentSettingsObject environmentSettingsObject
) {
  
  public GlobalObject globalObject() {
    return environmentSettingsObject.globalObject();
  }

  public static ScriptingContext create(
    FetchEngine fetchEngine,
    EnvironmentSettingsObject environmentSettingsObject
  ) {
    return new ScriptingContext(fetchEngine, environmentSettingsObject);
  }
  
}
