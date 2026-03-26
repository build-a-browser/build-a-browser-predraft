package net.buildabrowser.babbrowser.html.scripting.imp;

import net.buildabrowser.babbrowser.html.scripting.EnvironmentSettingsObject;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.html.scripting.Realm;

public class RealmImp implements Realm {

  private final GlobalObject globalObject;
  
  private EnvironmentSettingsObject hostDefined;

  public RealmImp(GlobalObject globalObject) {
    this.globalObject = globalObject;
  }

  @Override
  public GlobalObject globalObject() {
    return this.globalObject;
  }

  @Override
  public void setHostDefined(EnvironmentSettingsObject settingsObject) {
    this.hostDefined = settingsObject;
  }

  @Override
  public EnvironmentSettingsObject hostDefined() {
    return this.hostDefined;
  }
  
}
