package net.buildabrowser.babbrowser.cssbase.microsyntax;

public record QualifiedName(
  String namespace,
  String name
) {

  public static QualifiedName create(String namespace, String name) {
    return new QualifiedName(namespace, name);
  }
  
}
