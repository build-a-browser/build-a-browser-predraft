package net.buildabrowser.babbrowser.html.scripting;

// TODO: If scripting ever gets integrated, all this stuff will obviously
// need to be moved their. For now, it exists to mirror the spec.
public record Realm(
  GlobalObject globalObject
) {

  public static Realm create(GlobalObject globalObject) {
    return new Realm(globalObject);
  }

}
