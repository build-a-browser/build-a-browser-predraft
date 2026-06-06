package net.buildabrowser.babbrowser.cssbase.property;

public interface MutablePropertyContainer {

  void setProperty(CSSProperty property, CSSValue value);

  void setCustomProperty(String property, CSSValue value);

  CSSValue getProperty(PropertyContainer parent, CSSProperty property);

  CSSValue getCustom(String property);
  
}