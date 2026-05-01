package net.buildabrowser.babbrowser.cssbase.property;

public interface PropertyContainer {

  PropertyContainer parent();

  void setProperty(CSSProperty property, CSSValue value);

  void setCustomProperty(String property, CSSValue value);

  CSSValue getProperty(CSSProperty property);

  CSSValue getCustomProperty(String property);
  
}