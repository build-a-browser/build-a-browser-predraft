package net.buildabrowser.babbrowser.cssbase.property.test;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;

public class TestPropertyContainer implements PropertyContainer {

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
  public CSSValue getProperty(CSSProperty property) {
    return valueMap.get(property);
  }

  @Override
  public CSSValue getCustomProperty(String property) {
    if (!customValueMap.containsKey(property)) {
      return CSSFailure.UNSET_CUSTOM_PROPERTY;
    }
    return customValueMap.get(property);
  }

}
