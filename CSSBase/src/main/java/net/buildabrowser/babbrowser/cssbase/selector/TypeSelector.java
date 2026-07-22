package net.buildabrowser.babbrowser.cssbase.selector;

import net.buildabrowser.babbrowser.cssbase.microsyntax.QualifiedName;
import net.buildabrowser.babbrowser.infra.Namespace;

// TODO: Qualified name
public record TypeSelector(QualifiedName tagName) implements SimpleSelector {
  
  public static TypeSelector create(String tagName) {
    return new TypeSelector(QualifiedName.create(Namespace.HTML_NAMESPACE, tagName));
  }

  public static TypeSelector create(QualifiedName tagName) {
    return new TypeSelector(tagName);
  }

}
