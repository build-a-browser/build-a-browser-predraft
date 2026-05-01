package net.buildabrowser.babbrowser.html.scripting;

public record RealmExecutionContext(Realm realm) {

  public static RealmExecutionContext create(Realm realm) {
    return new RealmExecutionContext(realm);
  }

}
