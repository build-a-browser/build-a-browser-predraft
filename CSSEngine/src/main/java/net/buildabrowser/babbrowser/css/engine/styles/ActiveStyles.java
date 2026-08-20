package net.buildabrowser.babbrowser.css.engine.styles;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public interface ActiveStyles extends MutablePropertyContainer {

  Collection<WeightedStyleRule> refRules();

  void inheritProperty(CSSProperty property);

  void useInitialProperty(CSSProperty property);

  void useInitialCustomProperty(String property);

  void unsetProperty(CSSProperty property);

  CSSValue getProperty(CSSProperty property);

  CSSValue getCustom(String property);

  void forEachSet(BiConsumer<CSSProperty, CSSValue> itFunc);

  void freeze();

  PropertyContainer flatten(
    PropertyContainer parent,
    Function<PropertyContainer, PropertyContainer> cacheFunc
  );

  Map<String, CSSValue> customProperties();

  static ActiveStyles create() {
    return new ActiveStylesImp(null);
  }

  static ActiveStyles create(Collection<WeightedStyleRule> refRules) {
    return new ActiveStylesImp(refRules);
  }

  static PropertyContainer parentStyles(
    PropertyContainer parentContainer,
    ActiveStyles activeStyles
  ) {
    return activeStyles.flatten(parentContainer, a -> a);
  }

  static PropertyContainer unparentedStyles(
    ActiveStyles activeStyles
  ) {
    return activeStyles.flatten(null, a -> a);
  }

}
