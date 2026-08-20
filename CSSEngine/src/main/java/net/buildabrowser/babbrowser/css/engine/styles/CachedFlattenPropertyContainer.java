package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public interface CachedFlattenPropertyContainer extends PropertyContainer {

  PropertyContainer get(ActiveStyles activeStyles);

  void put(ActiveStyles activeStyles, PropertyContainer props);
  
}
