package net.buildabrowser.babbrowser.html.scripting;

import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.imp.WindowImp;

public interface Window extends GlobalObject {

  SimilarOriginWindowAgent agent();
  
  HTMLDocument associatedDocument();

  static Window create(
    SimilarOriginWindowAgent agent,
    HTMLDocument document
  ) {
    return new WindowImp(agent, document);
  }

  static void setupWindowEnvironmentSettingsObject(
    RealmExecutionContext executionContext
  ) {
    Realm realm = executionContext.realm();
    EnvironmentSettingsObject settingsObject = EnvironmentSettingsObject.create(executionContext);
    realm.setHostDefined(settingsObject);
  }

}
