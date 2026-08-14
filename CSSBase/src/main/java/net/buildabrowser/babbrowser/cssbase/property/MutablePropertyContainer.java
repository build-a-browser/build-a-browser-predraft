package net.buildabrowser.babbrowser.cssbase.property;

public interface MutablePropertyContainer {

  void setProperty(CSSProperty property, CSSValue value);

  default void setCustomProperty(String property, CSSValue value) {};
  
}