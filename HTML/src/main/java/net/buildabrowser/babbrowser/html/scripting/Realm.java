package net.buildabrowser.babbrowser.html.scripting;

import net.buildabrowser.babbrowser.html.scripting.imp.RealmImp;

// TODO: If scripting ever gets integrated, all this stuff will obviously
// need to be moved there. For now, it exists to mirror the spec.
public interface Realm {

  GlobalObject globalObject();

  void setHostDefined(EnvironmentSettingsObject settingsObject);

  EnvironmentSettingsObject hostDefined();

  public static Realm create(GlobalObject globalObject) {
    return new RealmImp(globalObject);
  }

}
