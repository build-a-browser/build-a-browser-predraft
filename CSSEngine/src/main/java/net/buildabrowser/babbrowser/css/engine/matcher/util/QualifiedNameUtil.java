package net.buildabrowser.babbrowser.css.engine.matcher.util;

import net.buildabrowser.babbrowser.cssbase.microsyntax.QualifiedName;
import net.buildabrowser.babbrowser.dom.Element;

public final class QualifiedNameUtil {
  
  private QualifiedNameUtil() {}

  public static boolean nameMatches(
    QualifiedName qualifiedName, Element element
  ) {
    // TODO: I think there was also a ** wildcard that I need to add
    String name = qualifiedName.name();
    String namespace = qualifiedName.namespace();
    return
      (name.equals(element.name()) || name.equals("*"))
      && (namespace.equals(element.namespace()) || namespace.equals("*"));
  }

}
