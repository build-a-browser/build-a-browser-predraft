package net.buildabrowser.babbrowser.cssbase.property.test;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class TestPropertyContainer implements PropertyContainer, MutablePropertyContainer {

  private final PropertyContainer parent;

  private final Map<CSSProperty, CSSValue> valueMap = new HashMap<>();
  private final Map<String, CSSValue> customValueMap = new HashMap<>();

  public TestPropertyContainer(PropertyContainer parent) {
    this.parent = parent;
  }

  @Override
  public PropertyContainer parent() {
    return this.parent;
  }

  @Override
  public void setProperty(CSSProperty property, CSSValue value) {
    valueMap.put(property, value);
  }

  @Override
  public void setCustomProperty(String property, CSSValue value) {
    customValueMap.put(property, value);
  }

  @Override
  public CSSValue get(CSSProperty property) {
    return valueMap.get(property);
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return false;
  }

  @Override
  public CSSValue getCustom(String property) {
    if (!customValueMap.containsKey(property)) {
      return CSSFailure.UNSET_CUSTOM_PROPERTY;
    }
    return customValueMap.get(property);
  }

}
