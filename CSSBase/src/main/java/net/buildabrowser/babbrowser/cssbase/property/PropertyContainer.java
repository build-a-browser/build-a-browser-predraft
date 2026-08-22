package net.buildabrowser.babbrowser.cssbase.property;

public interface PropertyContainer {

  PropertyContainer parent();

  boolean wasInherited(CSSProperty property);

  boolean wasSet(CSSProperty property);
  
  CSSValue get(CSSProperty property);

  CSSValue getCustom(String property);

  default boolean isReusable() {
    return false;
  }

}
