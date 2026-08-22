package net.buildabrowser.babbrowser.cssbase.microsyntax;

import net.buildabrowser.babbrowser.infra.Namespace;

public record QualifiedName(
  String namespace,
  String name
) {

  public static QualifiedName create(String namespace, String name) {
    return new QualifiedName(namespace, name);
  }

  public String serialize() {
    if (namespace.equals(Namespace.HTML_NAMESPACE)) {
      return name;
    } else {
      return namespace + ":" + name;
    }
  }
  
}
